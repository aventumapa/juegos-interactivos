# Dirección artística seleccionada — Atlas Nocturno

Atlas Nocturno combina cartografía oficial con una aventura luminosa, propia y modular, preparada para crecer a municipios y países.

## Personalidad

Una expedición tecnológica y mágica. Combina mapas iluminados, rutas curvas, estrellas y tarjetas profundas con una apariencia infantil que no se siente para bebés.

## Sistema visual

- Noche atlas: `#061522` para el fondo principal.
- Tarjeta profunda: `#0D2A40` y `#123550` para jerarquía.
- Cian eléctrico: `#35D5F0` para exploración y selección.
- Esmeralda: `#39E6B0` para aciertos.
- Oro estrella: `#FFC857` para logros.
- Coral: `#FF7C7C` y violeta `#B47CFF` para juegos.

Las formas tienen esquinas amplias, contornos moderados y sombras cortas. La interfaz mantiene contraste accesible y no comunica estados únicamente mediante color.

El dashboard prioriza una sola vista limpia: perfil y métricas arriba, cuatro tarjetas ilustradas al centro y navegación inferior fija. Las ilustraciones propias de mapa, corona, cerebro y rompecabezas sustituyen los glifos genéricos y mantienen volumen, brillo y lectura a tamaño pequeño.

## Marca piloto

La marca principal es una letra A construida como mapa plegado, atravesada por una ruta que termina en una estrella. La brújula-personaje Explorín funciona como guía dentro de la experiencia. Ambos recursos se construyen con vectores de Compose para conservar nitidez.

Los cuatro personajes de voz comparten un retrato ilustrado propio: Matein Pompin usa cian, Andreita violeta y coral, Maximo dorado y Claudis esmeralda. Sus avatares fueron generados específicamente para AventuMapa y se incluyen como recursos locales optimizados.

## Rompecabezas

El rompecabezas abre en orientación horizontal. El mapa inicia completamente sin color y ocupa aproximadamente 68% del tablero. La bandeja presenta seis siluetas por tanda y repone la siguiente tanda al completar la anterior hasta colocar las 32 entidades; cada acierto enciende únicamente el estado colocado. Tiempo, avance global, número de tanda, pista y sonido permanecen en el lateral, mientras Pausa se ubica al pie del mapa. El cambio de orientación se limita a esta actividad y se restaura al salir.

## Cartografía ilustrada

- Cada entidad conserva el contorno derivado de INEGI.
- Las regiones usan una paleta propia de siete familias cromáticas.
- Bordes blancos, sombras azul noche y destellos de selección crean una lectura tipo pieza coleccionable.
- Las tarjetas reutilizan la silueta real como medallón; no se usan imágenes genéricas ni iconos descargados.
- La lupa educativa amplía la silueta de entidades pequeñas sin modificar el mapa nacional.

## Audio propio

Los cuatro efectos breves de interacción se sintetizan con el generador del proyecto y no dependen de librerías de sonidos. La narración utiliza el motor local del dispositivo con perfiles de tono y ritmo diseñados para AventuMapa.

## Movimiento

Transiciones de 180–440 ms, giro tridimensional moderado en tarjetas, ampliación al arrastrar y celebraciones breves. Toda animación deberá respetar la preferencia de reducción de movimiento.
