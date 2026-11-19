# Servidor PlasSB Ltd. · Prototipo 2 · Equipo DS-14

Servicio externo de la planta PlasSB. Spring Boot + JPA + H2 (en disco, carpeta `data/`). Puerto 8081.

```
./gradlew bootRun          # Windows: gradlew.bat bootRun
```
Swagger UI: http://localhost:8081/swagger-ui.html · Consola H2: http://localhost:8081/h2-console

## API REST
| Endpoint | Parámetros | Retorno |
| --- | --- | --- |
| `GET /api/capacidad` | `fecha` (AAAA-MM-DD) | 200 `{fecha, toneladas}` · 404 sin capacidad publicada |
| `POST /api/notificaciones` | body `{fecha, numContenedores, totalEnvases, toneladas}` | 201 notificación registrada · 400 datos inválidos o capacidad insuficiente |
| `GET /api/notificaciones` | — | 200 lista de notificaciones recibidas |
