# Servidor ContSocket Ltd. · Prototipo 2 · Equipo DS-14

Servicio externo de la planta ContSocket. Java con sockets TCP y datos en memoria (sin base de datos). Puerto 9090 (`src/main/resources/config.properties`).

```
./gradlew run              # Windows: gradlew.bat run
```

## Protocolo
Una petición por conexión, texto enviado con `DataOutputStream.writeUTF` y leído con `readUTF`.

| Petición | Respuesta |
| --- | --- |
| `CAPACIDAD;AAAA-MM-DD` | `OK;toneladas` · `SIN_CAPACIDAD` |
| `NOTIFICAR;AAAA-MM-DD;contenedores;envases;toneladas` | `OK` · `ERROR;mensaje` |
| Cualquier otra | `ERROR;mensaje` |
