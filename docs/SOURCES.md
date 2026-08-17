# Fuentes y trazabilidad

## Contenido geográfico

El paquete usa las claves de las 32 áreas geoestadísticas estatales y los nombres usuales de sus capitales, cotejados con:

- INEGI, Marco Geoestadístico y Catálogo Único de Claves de Áreas Geoestadísticas Estatales, Municipales y Localidades.
- Fuentes oficiales estatales para denominaciones formales de capitales cuando exista más de una forma de uso.

## Geometría del mapa

- Fuente: Servicio Web del Catálogo Único de Claves Geoestadísticas de INEGI.
- Recurso vectorial AGEE: `https://gaia.inegi.org.mx/wscatgeo/v2/geo/mgee/`.
- Fecha de consulta: 2026-08-17.
- Transformación reproducible: `tools/generate_mexico_map.py`.
- El proceso conserva los 32 contornos, normaliza coordenadas y aplica simplificación Douglas–Peucker para ejecución móvil.
- El mapa nacional mantiene una sola escala; las entidades pequeñas se amplían únicamente en una lupa separada o como pieza manipulable.

Los archivos de sonido se generan localmente mediante `tools/generate_sound_effects.py` y son originales del proyecto.

## Arte original

Los retratos de Matein Pompin, Andreita, Maximo y Claudis fueron generados específicamente para AventuMapa a partir de su dirección artística propia. No representan personas reales ni personajes de otra franquicia. Los originales de producción se reducen a 640 × 640 píxeles para uso local dentro del APK.

## Dependencias

El proyecto utiliza AndroidX, Kotlin, Kotlin Coroutines, Dagger/Hilt y JUnit. Sus versiones se centralizan en `gradle/libs.versions.toml` y se revisarán antes de cada publicación.
