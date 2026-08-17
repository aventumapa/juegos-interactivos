# Voces naturales

## Implementación actual

AventuMapa consulta las voces que expone el motor TTS de Android y ordena las voces en español por calidad, preferencia `es-MX`, latencia y disponibilidad neural/conectada. Si una voz de red falla, la misma frase se repite con la mejor voz local instalada.

Los personajes conservan nombres estables aunque el timbre exacto dependa del dispositivo:

- Matein Pompin: voz niño, tono 1.08 y velocidad 0.95.
- Andreita: voz niña, tono 1.11 y velocidad 0.97.
- Maximo: voz elegante, tono 0.94 y velocidad 0.91.
- Claudis: voz amigable, tono 1.02 y velocidad 0.94.

Los ajustes son deliberadamente moderados para evitar el efecto robótico que producían los tonos anteriores.

## Evolución a voz de estudio

Para que cada personaje conserve exactamente el mismo timbre en todos los teléfonos se requiere un proveedor neural en la nube o un catálogo de audios preproducidos. Las credenciales nunca deben incluirse en el APK; la aplicación deberá solicitar el audio a un backend seguro, almacenarlo temporalmente y regresar al TTS local cuando no haya conexión.

La integración futura debe conservar:

1. español de México o español latino;
2. frases breves y ritmo calmado;
3. caché local para reducir costo y espera;
4. consentimiento del adulto para funciones conectadas;
5. ausencia de clonación de voces reales sin autorización.
