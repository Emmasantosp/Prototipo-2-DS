# Servidor Ecoembes · Prototipo 2 · Equipo DS-14

Proyecto Java 21 + Spring Boot 3.5 + Gradle 9 (funciona con JDK 21 o superior, incluido Java 25). Persistencia con JPA + H2 en disco (carpeta `data/`). La capacidad de las plantas y las notificaciones pasan por los servidores PlasSB (REST, puerto 8081) y ContSocket (sockets, puerto 9090), que deben estar arrancados antes.

## Ejecutar
```
./gradlew bootRun          # Windows: gradlew.bat bootRun
```
Swagger UI: http://localhost:8080/swagger-ui.html · Consola H2: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:file:./data/ecoembes`, usuario `sa`, sin contraseña)

## Usuarios de prueba
lorea@ecoembes.com / 1234 · jon@ecoembes.com / 4321 · iker@ecoembes.com / 1122

## Paquetes
- `com.ecoembes.facade`: fachada remota (EcoembesController, ContenedorController) y gestor de errores
- `com.ecoembes.service`: AppServices (EcoembesService, ContenedorService)
- `com.ecoembes.dao`: DAOs (repositorios Spring Data JPA)
- `com.ecoembes.gateway`: Service Gateways de las plantas (IPlantaGateway, PlasSBGateway por REST, ContSocketGateway por sockets) y su factoría (PlantaGatewayFactory)
- `com.ecoembes.config`: carga de datos iniciales en la BD
- `com.ecoembes.state`: ServerState (tokens activos)
- `com.ecoembes.dto`: DTOs del API
- `com.ecoembes.entity`: dominio
- `com.ecoembes.exception`: excepciones de la aplicación (400, 401, 404, 503)

## Direcciones de las plantas
En `src/main/resources/application.properties`: `plantas.plassb.url`, `plantas.contsocket.host` y `plantas.contsocket.port`.
