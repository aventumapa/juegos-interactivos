#!/usr/bin/env python3
"""Generate compact, normalized map geometry from INEGI's AGEE GeoJSON service."""

from __future__ import annotations

import json
import math
import sys
from pathlib import Path


def perpendicular_distance(point, start, end):
    if start == end:
        return math.dist(point, start)
    dx = end[0] - start[0]
    dy = end[1] - start[1]
    projection = ((point[0] - start[0]) * dx + (point[1] - start[1]) * dy) / (dx * dx + dy * dy)
    nearest = (start[0] + projection * dx, start[1] + projection * dy)
    return math.dist(point, nearest)


def simplify(points, epsilon):
    if len(points) <= 2:
        return points
    start, end = points[0], points[-1]
    distance, index = max(
        ((perpendicular_distance(point, start, end), index) for index, point in enumerate(points[1:-1], 1)),
        default=(0.0, 0),
    )
    if distance <= epsilon:
        return [start, end]
    left = simplify(points[: index + 1], epsilon)
    right = simplify(points[index:], epsilon)
    return left[:-1] + right


def signed_area(ring):
    return sum(
        first[0] * second[1] - second[0] * first[1]
        for first, second in zip(ring, ring[1:] + ring[:1])
    ) / 2.0


def polygon_centroid(ring):
    area = signed_area(ring)
    if abs(area) < 1e-12:
        return (
            sum(point[0] for point in ring) / len(ring),
            sum(point[1] for point in ring) / len(ring),
        )
    factor_sum_x = 0.0
    factor_sum_y = 0.0
    for first, second in zip(ring, ring[1:] + ring[:1]):
        factor = first[0] * second[1] - second[0] * first[1]
        factor_sum_x += (first[0] + second[0]) * factor
        factor_sum_y += (first[1] + second[1]) * factor
    return factor_sum_x / (6.0 * area), factor_sum_y / (6.0 * area)


def exterior_rings(geometry):
    coordinates = geometry["coordinates"]
    if geometry["type"] == "Polygon":
        return [coordinates[0]]
    if geometry["type"] == "MultiPolygon":
        return [polygon[0] for polygon in coordinates]
    raise ValueError(f"Unsupported geometry: {geometry['type']}")


def main(source_path, destination_path):
    with Path(source_path).open(encoding="utf-8") as source_file:
        source = json.load(source_file)

    all_points = [
        point
        for feature in source["features"]
        for ring in exterior_rings(feature["geometry"])
        for point in ring
    ]
    min_x = min(point[0] for point in all_points)
    max_x = max(point[0] for point in all_points)
    min_y = min(point[1] for point in all_points)
    max_y = max(point[1] for point in all_points)

    def normalize(point):
        return [
            round((point[0] - min_x) / (max_x - min_x), 5),
            round(1.0 - (point[1] - min_y) / (max_y - min_y), 5),
        ]

    states = []
    for feature in sorted(source["features"], key=lambda item: item["properties"]["cve_ent"]):
        source_rings = sorted(exterior_rings(feature["geometry"]), key=lambda ring: abs(signed_area(ring)), reverse=True)
        kept_rings = [ring for index, ring in enumerate(source_rings) if index < 12 and (index == 0 or abs(signed_area(ring)) >= 0.00015)]
        normalized_rings = []
        weighted_centroid_x = 0.0
        weighted_centroid_y = 0.0
        total_area = 0.0

        for ring in kept_rings:
            open_ring = [tuple(point) for point in ring[:-1]]
            simplified = simplify(open_ring + [open_ring[0]], epsilon=0.012)
            if len(simplified) < 4:
                continue
            normalized_rings.append([normalize(point) for point in simplified])
            area = abs(signed_area(open_ring))
            centroid_x, centroid_y = polygon_centroid(open_ring)
            weighted_centroid_x += centroid_x * area
            weighted_centroid_y += centroid_y * area
            total_area += area

        centroid = normalize((weighted_centroid_x / total_area, weighted_centroid_y / total_area))
        flat_points = [point for ring in normalized_rings for point in ring]
        bounds = [
            min(point[0] for point in flat_points),
            min(point[1] for point in flat_points),
            max(point[0] for point in flat_points),
            max(point[1] for point in flat_points),
        ]
        states.append(
            {
                "code": feature["properties"]["cve_ent"],
                "centroid": centroid,
                "bounds": [round(value, 5) for value in bounds],
                "polygons": normalized_rings,
            }
        )

    payload = {
        "source": "INEGI Catálogo Único de Claves Geoestadísticas, servicio vectorial AGEE",
        "sourceUrl": "https://gaia.inegi.org.mx/wscatgeo/v2/geo/mgee/",
        "sourceRetrieved": "2026-08-17",
        "originalBounds": [min_x, min_y, max_x, max_y],
        "states": states,
    }
    destination = Path(destination_path)
    destination.parent.mkdir(parents=True, exist_ok=True)
    destination.write_text(json.dumps(payload, ensure_ascii=False, separators=(",", ":")), encoding="utf-8")


if __name__ == "__main__":
    if len(sys.argv) != 3:
        raise SystemExit("Usage: generate_mexico_map.py INPUT.geojson OUTPUT.json")
    main(sys.argv[1], sys.argv[2])
