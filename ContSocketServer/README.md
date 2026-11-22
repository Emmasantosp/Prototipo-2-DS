# Servidor ContSocket Ltd. · Prototipo 2 · Equipo DS-14

Servicio externo de la planta ContSocket. Java con sockets TCP y persistencia en memoria (sin base de datos): un mapa con la capacidad de cada día y una lista con las notificaciones recibidas. Los datos se pierden al apagar el servicio. Puerto 9090 (`src/main/resources/config.properties`).

```
./gradlew run              # Windows: gradlew.bat run
```

## Protocolo
Un mensaje por conexión, texto enviado con `DataOutputStream.writeUTF` y leído con `readUTF`. Campos separados por `;`.

| Mensaje | Respuesta correcta | Respuestas de error |
| --- | --- | --- |
| `CONSULTAR_CAPACIDAD;fecha` | `OK;fecha;capacidadToneladas` | `ERROR;400;Fecha inválida` · `ERROR;404;No hay capacidad publicada para esa fecha` |
| `NOTIFICAR_ASIGNACION;fecha;numContenedores;totalEnvases` | `OK;idNotificacion` | `ERROR;400;Datos inválidos` |
| Cualquier otro | — | `ERROR;400;Comando desconocido` |
