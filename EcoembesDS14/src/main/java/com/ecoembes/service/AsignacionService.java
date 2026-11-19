package com.ecoembes.service;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecoembes.dao.AsignacionRepository;
import com.ecoembes.entity.AsignacionPlanta;
import com.ecoembes.entity.CapacidadPlanta;
import com.ecoembes.entity.Contenedor;
import com.ecoembes.entity.Empleado;
import com.ecoembes.entity.PlantaReciclaje;
import com.ecoembes.exception.PeticionInvalidaException;

/** AppService de asignación de contenedores a plantas de reciclaje. */
@Service
public class AsignacionService {

    //Suponemos que el peso medio de un envase es de 25 g (0,000025 t)
    public static final double TONELADAS_POR_ENVASE = 0.000025;

    private final ContenedorService contenedorService;
    private final PlantaService plantaService;
    //Desde el Prototipo 2 las asignaciones se guardan en la base de datos (DAO)
    private final AsignacionRepository asignacionRepository;

    public AsignacionService(ContenedorService contenedorService, PlantaService plantaService,
                             AsignacionRepository asignacionRepository) {
        this.contenedorService = contenedorService;
        this.plantaService = plantaService;
        this.asignacionRepository = asignacionRepository;
    }

    @Transactional
    public synchronized AsignacionPlanta asignar(String idPlanta, LocalDate fecha, List<String> idsContenedores, Empleado empleado) {
        PlantaReciclaje planta = plantaService.getPlanta(idPlanta); //Busca la planta. Si no existe 404
        List<Contenedor> contenedores = new LinkedHashSet<>(idsContenedores).stream() //Quita los ids repetidos manteniendo el orden
                .map(contenedorService::getContenedor) //Convierte cada id en su objeto Contenedor. Si alguno no existe 404
                .toList();
        CapacidadPlanta capacidad = plantaService.buscarCapacidad(planta, fecha).orElseThrow(() ->
                new PeticionInvalidaException("La planta no ha publicado capacidad para " + fecha)); /**Pide la capacidad de ese día a la planta (gateway).
        Si no hay capacidad publicada, lanza PeticionInvalidaException --> 400*/

        int totalEnvases = contenedores.stream().mapToInt(Contenedor::getNumEnvases).sum(); //Suma el nº de envases de todos los contenedores asignados
        double toneladas = toneladasEstimadas(totalEnvases); //Lo pasa a toneladas (redondeando a 3 decimales) para comparar con la capacidad de la planta
        if (toneladas > capacidad.getToneladas()) {
            throw new PeticionInvalidaException(String.format(
                    "Capacidad insuficiente en %s para %s: se necesitan %.3f t y quedan %.3f t",
                    planta.getNombre(), fecha, toneladas, capacidad.getToneladas()));
        }

        //Notifica a la planta a través de su gateway. La planta descuenta las toneladas de su capacidad.
        //Se hace antes de guardar: si la planta rechaza la asignación, no queda nada registrado.
        plantaService.notificarAsignacion(planta, fecha, contenedores.size(), totalEnvases, toneladas);

        AsignacionPlanta asignacion = new AsignacionPlanta(UUID.randomUUID().toString(), fecha, planta, contenedores,
                empleado, redondear(capacidad.getToneladas() - toneladas));
        asignacionRepository.save(asignacion); //Guarda la asignación (con auditoría) en la base de datos
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
