# AventuMapa

AventuMapa es una aplicación Android educativa, local y sin anuncios para que niñas y niños de 6 a 12 años aprendan la geografía de México mediante exploración y juegos.

## Estado actual

Este repositorio contiene el primer corte vertical jugable:

- Perfil infantil local con alias y avatar.
- Inicio con experiencia, estrellas y progreso.
- Explorador inicial de las 32 entidades y sus capitales.
- Reto de opción múltiple de capitales.
- Memorama entidad–capital.
- Persistencia local con DataStore.
- Motor de juegos determinista y probado.
- Política de ampliación para entidades pequeñas sin alterar su escala geográfica.

El mapa vectorial nacional se integrará únicamente con geometrías oficiales validadas. No se incluyen contornos aproximados.

## Requisitos

- Android Studio compatible con AGP 8.13.2.
- JDK 17.
- Android SDK 36.

El proyecto usa Compose BOM 2026.04.01 porque es la línea estable compatible con AGP 8.13.2 y API 36. Las líneas de Compose que requieren API 37 se adoptarán junto con la migración correspondiente de AGP.

## Ejecución

1. Abre la carpeta `AventuMapa` en Android Studio.
2. Espera la sincronización de Gradle.
3. Ejecuta la configuración `app` en un dispositivo o emulador con Android 8.0 o posterior.

Desde terminal:

```bash
./gradlew :app:assembleDebug
./gradlew test
```

## Decisiones técnicas

- Kotlin y Jetpack Compose con Material 3.
- MVVM y flujo de estado unidireccional.
- Hilt para inyección de dependencias.
- DataStore para el progreso del corte vertical.
- Módulos iniciales: `app`, `core:model`, `content:mexico` y `game-engine`.
- Room se incorporará cuando el importador versionado de contenido y las consultas de progreso lo justifiquen.
- La aplicación no depende de Firebase ni de una cuenta.

Consulta `docs/ARCHITECTURE.md`, `docs/ART_DIRECTION.md` y `docs/SOURCES.md` para más detalles.
