# Arquitectura del corte vertical

## Módulos

- `app`: interfaz Compose, navegación, persistencia local e integración.
- `core:model`: modelos de dominio sin dependencias de Android.
- `content:mexico`: paquete versionado inicial de entidades y capitales.
- `game-engine`: creación reproducible de partidas, dominio y política de entidades pequeñas.

Los módulos `core:database`, `core:maps`, `core:designsystem` y las funciones independientes se extraerán conforme el MVP lo necesite. Esta primera división protege el dominio y evita modularización prematura.

## Estado y datos

Las pantallas envían eventos explícitos al `AppViewModel`. El ViewModel actualiza repositorios y expone un estado inmutable mediante `StateFlow`. DataStore conserva alias, avatar, XP, estrellas, rondas y aciertos sin recopilar datos personales ni usar internet.

## Estrategia cartográfica implementada

El mapa usa geometrías vectoriales oficiales de INEGI convertidas mediante una herramienta reproducible a coordenadas normalizadas y rutas optimizadas. El motor conserva por separado:

1. Geometría visible real.
2. Geometría simplificada por nivel de zoom.
3. Área táctil accesible.
4. Recuadro educativo ampliado.

La geometría visible nunca se agrandará dentro del mapa nacional. Cuando una entidad sea demasiado pequeña, la selección mostrará su posición real y un recuadro conectado con la silueta ampliada sin deformación.

## Estado de la versión 0.2

El explorador cartográfico, el cuestionario hablado, el memorama animado, el rompecabezas, la narración TTS, los efectos y la persistencia son funcionales. Room, contenido municipal, reducción de movimiento y el panel adulto avanzado pertenecen a las siguientes entregas.
