# Prototipo 2 · Proyecto Ecoembes · Equipo DS-14

Tres proyectos independientes (Java 21+, Gradle). Orden de arranque:

1. `ContSocketServer`: `gradlew run` (sockets, puerto 9090)
2. `PlasSBServer`: `gradlew bootRun` (REST, puerto 8081)
3. `EcoembesDS14`: `gradlew bootRun` (REST, puerto 8080)

En Eclipse se importan con File → Import → Existing Gradle Project.

Si se ha arrancado antes una versión anterior, borrar la carpeta `data/` de EcoembesDS14 y de PlasSBServer
(bases de datos H2) para que se creen de nuevo con el esquema y los datos de prueba actuales.
