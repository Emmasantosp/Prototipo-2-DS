package com.ecoembes.service;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ecoembes.dao.AsignacionRepository;
import com.ecoembes.dao.EmpleadoRepository;
import com.ecoembes.dao.PlantaRepository;
import com.ecoembes.entity.AsignacionPlanta;
import com.ecoembes.entity.CapacidadPlanta;
import com.ecoembes.entity.Contenedor;
import com.ecoembes.entity.Empleado;
import com.ecoembes.entity.PlantaReciclaje;
import com.ecoembes.exception.PeticionInvalidaException;
import com.ecoembes.exception.RecursoNoEncontradoException;
import com.ecoembes.exception.TokenInvalidoException;
import com.ecoembes.gateway.IPlantaGateway;
import com.ecoembes.gateway.PlantaGatewayFactory;
import com.ecoembes.state.ServerState;

/**
 * AppService de Ecoembes: autenticación del personal, plantas de reciclaje
 * y asignación de contenedores a plantas.
 * Desde el Prototipo 2 los datos están en la base de datos (DAOs) y las plantas se consultan
 * a través de su Service Gateway. Este servicio solo conoce la interfaz IPlantaGateway:
 * no sabe si la planta usa REST o sockets (eso lo decide la factoría).
 */
@Service
public class EcoembesService {

    //Suponemos que el peso medio de un envase es de 25 g (0,000025 t)
    public static final double TONELADAS_POR_ENVASE = 0.000025;

    private final ServerState serverState;
    private final ContenedorService contenedorService;
    private final EmpleadoRepository empleadoRepository;
    private final PlantaRepository plantaRepository;
    private final AsignacionRepository asignacionRepository;
    private final PlantaGatewayFactory gatewayFactory;

    public EcoembesService(ServerState serverState, ContenedorService contenedorService,
                           EmpleadoRepository empleadoRepository, PlantaRepository plantaRepository,
                           AsignacionRepository asignacionRepository, PlantaGatewayFactory gatewayFactory) {
        this.serverState = serverState;
        this.contenedorService = contenedorService;
        this.empleadoRepository = empleadoRepository;
        this.plantaRepository = plantaRepository;
        this.asignacionRepository = asignacionRepository;
        this.gatewayFactory = gatewayFactory;
    }

    //AUTENTICACIÓN

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

    //PLANTAS DE RECICLAJE

    public List<PlantaReciclaje> getPlantas() {
        return plantaRepository.findAllByOrderByIdAsc();
    }

    public PlantaReciclaje getPlanta(String idPlanta) {
        return plantaRepository.findById(idPlanta)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la planta " + idPlanta));
    }

    //Capacidad disponible (en toneladas) de una planta para un día concreto. Si no hay capacidad publicada para ese día lanza 404
    public CapacidadPlanta consultarCapacidad(String idPlanta, LocalDate fecha) {
        PlantaReciclaje planta = getPlanta(idPlanta); //Busca la planta en la BD. Si no existe 404
        return capacidadDisponible(planta, fecha).orElseThrow(() ->
                new RecursoNoEncontradoException("La planta " + idPlanta + " no ha publicado capacidad para " + fecha));
    }

    //Pide a la planta (a través de su gateway) la capacidad publicada ese día
    //y le resta las toneladas que Ecoembes ya le ha asignado para ese mismo día
    private Optional<CapacidadPlanta> capacidadDisponible(PlantaReciclaje planta, LocalDate fecha) {
        IPlantaGateway gateway = gatewayFactory.getGateway(planta.getTipoServicio()); //La factoría devuelve el gateway adecuado
        return gateway.consultarCapacidad(fecha)
                .map(publicada -> new CapacidadPlanta(planta, fecha,
                        redondear(Math.max(0, publicada - toneladasAsignadas(planta, fecha)))));
    }

    //Toneladas ya asignadas a una planta para un día (suma de las asignaciones guardadas en la BD)
    private double toneladasAsignadas(PlantaReciclaje planta, LocalDate fecha) {
        return asignacionRepository.findByPlantaIdAndFecha(planta.getId(), fecha).stream()
                .mapToDouble(a -> toneladasEstimadas(a.getTotalEnvases()))
                .sum();
    }

    //ASIGNACIONES

    public synchronized AsignacionPlanta asignar(String idPlanta, LocalDate fecha, List<String> idsContenedores, Empleado empleado) {
        PlantaReciclaje planta = getPlanta(idPlanta); //Busca la planta. Si no existe 404
        List<Contenedor> contenedores = new LinkedHashSet<>(idsContenedores).stream() //Quita los ids repetidos manteniendo el orden
                .map(contenedorService::getContenedor) //Convierte cada id en su objeto Contenedor. Si alguno no existe 404
                .toList();
        CapacidadPlanta capacidad = capacidadDisponible(planta, fecha).orElseThrow(() ->
                new PeticionInvalidaException("La planta no ha publicado capacidad para " + fecha)); //Capacidad disponible ese día. Si no hay, 400

        int totalEnvases = contenedores.stream().mapToInt(Contenedor::getNumEnvases).sum(); //Suma el nº de envases de todos los contenedores asignados
        double toneladas = toneladasEstimadas(totalEnvases); //Lo pasa a toneladas (redondeando a 3 decimales) para comparar con la capacidad de la planta
        if (toneladas > capacidad.getToneladas()) {
            throw new PeticionInvalidaException(String.format(
                    "Capacidad insuficiente en %s para %s: se necesitan %.3f t y quedan %.3f t",
                    planta.getNombre(), fecha, toneladas, capacidad.getToneladas()));
        }

        //1. Guarda la asignación (con auditoría: quién la hizo) en la base de datos
        AsignacionPlanta asignacion = asignacionRepository.save(new AsignacionPlanta(UUID.randomUUID().toString(), fecha,
                planta, contenedores, empleado, redondear(capacidad.getToneladas() - toneladas)));

        //2. Notifica a la planta a través de su gateway el nº total de contenedores y de envases.
        //   Si la planta no responde o rechaza la notificación, se borra la asignación: no queda nada a medias.
        try {
            gatewayFactory.getGateway(planta.getTipoServicio()).notificarAsignacion(fecha, contenedores.size(), totalEnvases);
        } catch (RuntimeException e) {
            asignacionRepository.delete(asignacion);
            throw e;
        }
        return asignacion;
    }

    public double toneladasEstimadas(int envases) {
        return redondear(envases * TONELADAS_POR_ENVASE);
    }

    public double capacidadRestante(AsignacionPlanta a) {
        return a.getCapacidadRestanteToneladas();
    }

    private static double redondear(double toneladas) {
        return Math.round(toneladas * 1000) / 1000.0; //3 decimales (kg)
    }
}
