package com.ecoembes.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ecoembes.dao.EmpleadoRepository;
import com.ecoembes.entity.Empleado;
import com.ecoembes.exception.TokenInvalidoException;
import com.ecoembes.state.ServerState;

/** AppService de autenticación: login, logout y validación de tokens */
@Service
public class AuthService {

    private final ServerState serverState;
    //Desde el Prototipo 2 el personal se lee de la base de datos (DAO)
    private final EmpleadoRepository empleadoRepository;

    public AuthService(ServerState serverState, EmpleadoRepository empleadoRepository) {
        this.serverState = serverState;
        this.empleadoRepository = empleadoRepository;
    }

    /** Devuelve el token si las credenciales son correctas */
    public synchronized Optional<String> login(String email, String password) {
        Empleado empleado = empleadoRepository.findByEmail(email).orElse(null); //busca el empleado por email en la base de datos
        if (empleado == null || !empleado.checkPassword(password)) { //si el empleado no existe o la contraseña no coincide, devuelve Optional vacío que el Controller convierte en un 401
            return Optional.empty();
        }
        long ts = System.currentTimeMillis(); //coge la hora actual en milisegundos para generar un token único
        while (serverState.existeToken(String.valueOf(ts))) ts++; //evita que haya colisiones si dos personas hacen login en el mismo ms
        String token = String.valueOf(ts); //convierte el número de milisegundos en String para usarlo como token
        serverState.addToken(token, empleado); //el empleado queda en memoria: no hay que volver a la BD en cada petición
        return Optional.of(token);
    }

    /** Elimina el token, a partir de aquí ha caducado */
    public void logout(String token) { //en el logout elimina el token de la lista de tokens válidos; si no existe lanza 401
        if (!serverState.removeToken(token)) {
            throw new TokenInvalidoException();
        }
    }

    /** Valida el token y devuelve el empleado autenticado. Si no es válido lanza 401 */
    public Empleado getEmpleado(String token) {
        Empleado empleado = serverState.getEmpleado(token);
        if (empleado == null) {
            throw new TokenInvalidoException();
        }
        return empleado;
    }

    public Empleado getEmpleadoPorEmail(String email) {
        return empleadoRepository.findByEmail(email).orElse(null);
    }
}
