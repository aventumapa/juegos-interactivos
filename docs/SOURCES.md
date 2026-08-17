# Fuentes y trazabilidad

## Contenido geográfico

El paquete inicial usa las claves de las 32 áreas geoestadísticas estatales y los nombres usuales de sus capitales. Antes de publicar el MVP se validará cada registro contra:

- INEGI, Marco Geoestadístico y Catálogo Único de Claves de Áreas Geoestadísticas Estatales, Municipales y Localidades.
- Fuentes oficiales estatales para denominaciones formales de capitales cuando exista más de una forma de uso.

Las geometrías todavía no forman parte del corte vertical. Se incorporarán desde una descarga oficial versionada del Marco Geoestadístico, conservando fecha de consulta, versión, transformación, simplificación y licencia aplicable.

## Dependencias

El proyecto utiliza AndroidX, Kotlin, Kotlin Coroutines, Dagger/Hilt y JUnit. Sus versiones se centralizan en `gradle/libs.versions.toml` y se revisarán antes de cada publicación.

