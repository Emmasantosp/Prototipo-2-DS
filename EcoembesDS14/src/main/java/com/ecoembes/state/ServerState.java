package com.ecoembes.state;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.ecoembes.entity.Empleado;

/**
 * Patrón State Management: estado del servidor en memoria.
 * Guarda los tokens activos y el empleado autenticado con cada uno, para no tener que
 * consultar la información del empleado en cada petición.
 */
@Component
public class ServerState {

    /** token (timestamp del login) -> empleado autenticado */
    private final Map<String, Empleado> tokensActivos = new ConcurrentHashMap<>();

    public void addToken(String token, Empleado empleado) { //al hacer login añade el token a la lista de tokens válidos
        tokensActivos.put(token, empleado);
    }

    public boolean removeToken(String token) { //al hacer logout elimina el token de la lista de tokens válidos. Si no existe lanza 401
        return tokensActivos.remove(token) != null;
    }

    public boolean existeToken(String token) {
        return token != null && tokensActivos.containsKey(token);
    }

    public Empleado getEmpleado(String token) {
        return token == null ? null : tokensActivos.get(token);
    }
}
