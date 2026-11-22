# Servidor PlasSB Ltd. · Prototipo 2 · Equipo DS-14

Servicio externo de la planta PlasSB. Spring Boot + JPA + H2 (en disco, carpeta `data/`). Puerto 8081.

```
./gradlew bootRun          # Windows: gradlew.bat bootRun
```
Swagger UI: http://localhost:8081/swagger-ui.html · Consola H2: http://localhost:8081/h2-console (JDBC URL: `jdbc:h2:file:./data/plassb`)

## API REST
| Endpoint | Parámetros | Retorno |
| --- | --- | --- |
| `GET /plassb/capacidad` | RequestParam `fecha` (AAAA-MM-DD) | 200 `CapacidadDTO {fecha, capacidadToneladas}` · 400 fecha inválida · 404 sin capacidad publicada |
| `POST /plassb/notificaciones` | Body `NotificacionDTO {fecha, numContenedores, totalEnvases}` | 201 notificación registrada · 400 datos inválidos |
| `GET /plassb/notificaciones` | — | 200 lista de notificaciones recibidas |

Los errores se devuelven como `ErrorDTO {status, error, mensaje}`.
