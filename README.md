# AventuMapa

AventuMapa es una aplicación Android educativa, local y sin anuncios para que niñas y niños de 6 a 12 años aprendan la geografía de México mediante exploración y juegos.

## Estado actual

Este repositorio contiene la versión jugable audiovisual 0.5.0:

- Perfil infantil local con alias y avatar.
- Identidad visual Atlas Nocturno con marca vectorial, Explorín y tarjetas luminosas.
- Inicio compacto inspirado en una aventura premium: saludo, XP, racha, cuatro actividades ilustradas y navegación inferior visibles en una sola pantalla.
- Mapa vectorial interactivo de las 32 entidades con geometría oficial de INEGI.
- Lupa educativa para CDMX, Tlaxcala, Morelos, Colima, Aguascalientes y Querétaro.
- Cuatro guías ilustrados y perfiles de narración: Matein Pompin, Andreita, Maximo y Claudis; su selector se abre desde el botón de audio del inicio.
- Selección automática de voces `es-MX` de mayor calidad, con soporte para voces neurales del dispositivo y regreso automático a una voz local si falla la conexión.
- Reto hablado de capitales con felicitaciones variables y personalizadas.
- Memorama entidad–capital con siluetas, animación, efectos originales y pronunciación.
- Rompecabezas horizontal para tablet con bandeja lateral, cronómetro, pausa, pista, piezas ampliadas y ajuste magnético.
- Persistencia local con DataStore.
- Motor de juegos determinista y probado.
- Política de ampliación para entidades pequeñas sin alterar la escala del mapa nacional.
- Nombres educativos breves para estados y capitales; los nombres oficiales se conservan internamente.
- Efectos de sonido originales generados para AventuMapa.

La disponibilidad exacta de timbres depende del motor de texto a voz instalado. AventuMapa prioriza voces `es-MX` de calidad alta o muy alta —incluidas las neurales que el dispositivo exponga— y usa ajustes de tono moderados para evitar fatiga. Si una voz conectada falla, repite la frase con la mejor voz local disponible.

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
