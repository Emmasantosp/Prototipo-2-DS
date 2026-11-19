package com.ecoembes.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ecoembes.dao.PlantaRepository;
import com.ecoembes.entity.CapacidadPlanta;
import com.ecoembes.entity.PlantaReciclaje;
import com.ecoembes.exception.RecursoNoEncontradoException;
import com.ecoembes.gateway.IPlantaGateway;
import com.ecoembes.gateway.PlantaGatewayFactory;

/**
 * AppService de plantas de reciclaje.
 * Desde el Prototipo 2 la capacidad se pide a cada planta a través de su Service Gateway.
 * Este servicio solo conoce la interfaz IPlantaGateway: no sabe si la planta usa REST o sockets.
 */
@Service
public class PlantaService {

    private final PlantaRepository plantaRepository;
    private final PlantaGatewayFactory gatewayFactory;

    public PlantaService(PlantaRepository plantaRepository, PlantaGatewayFactory gatewayFactory) {
        this.plantaRepository = plantaRepository;
        this.gatewayFactory = gatewayFactory;
    }

    public List<PlantaReciclaje> getPlantas() {
        return plantaRepository.findAllByOrderByIdAsc();
    }

    public PlantaReciclaje getPlanta(String idPlanta) {
        return plantaRepository.findById(idPlanta)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la planta " + idPlanta));
    }

    //Capacidad disponible (en toneladas) de una planta para un día concreto. Si no hay capacidad publicada para ese día lanza 404
    public CapacidadPlanta consultarCapacidad(String idPlanta, LocalDate fecha) {
        PlantaReciclaje planta = getPlanta(idPlanta);
        return buscarCapacidad(planta, fecha).orElseThrow(() ->
                new RecursoNoEncontradoException("La planta " + idPlanta + " no ha publicado capacidad para " + fecha));
    }

    //Pide la capacidad a la planta a través de su gateway
    public Optional<CapacidadPlanta> buscarCapacidad(PlantaReciclaje planta, LocalDate fecha) {
        IPlantaGateway gateway = gatewayFactory.getGateway(planta.getTipoServicio());
        return gateway.consultarCapacidad(fecha).map(toneladas -> new CapacidadPlanta(fecha, toneladas));
    }

    //Notifica a la planta, a través de su gateway, la asignación completada
    public void notificarAsignacion(PlantaReciclaje planta, LocalDate fecha, int numContenedores, int totalEnvases, double toneladas) {
        IPlantaGateway gateway = gatewayFactory.getGateway(planta.getTipoServicio());
        gateway.notificarAsignacion(fecha, numContenedores, totalEnvases, toneladas);
    }
}
