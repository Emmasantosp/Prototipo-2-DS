package com.ecoembes.gateway;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.ecoembes.exception.ServicioExternoException;

/**
 * Patrón Factoría: crea el Service Gateway adecuado según el tipo de servicio de la planta.
 * Es el único sitio del servidor donde se decide entre PlasSB y ContSocket (el switch solo está aquí).
 * Las direcciones y puertos de los servicios externos se leen de application.properties.
 */
@Component
public class PlantaGatewayFactory {

    private final RestClient.Builder restClientBuilder;
    private final String plassbUrl;
    private final String contsocketHost;
    private final int contsocketPuerto;
    //Cada gateway se crea una vez y se reutiliza
    private final Map<String, IPlantaGateway> gateways = new ConcurrentHashMap<>();

    public PlantaGatewayFactory(RestClient.Builder restClientBuilder,
                                @Value("${plantas.plassb.url}") String plassbUrl,
                                @Value("${plantas.contsocket.host}") String contsocketHost,
                                @Value("${plantas.contsocket.port}") int contsocketPuerto) {
        this.restClientBuilder = restClientBuilder;
        this.plassbUrl = plassbUrl;
        this.contsocketHost = contsocketHost;
        this.contsocketPuerto = contsocketPuerto;
    }

    public IPlantaGateway getGateway(String tipoServicio) {
        return gateways.computeIfAbsent(tipoServicio, this::crearGateway);
    }

    private IPlantaGateway crearGateway(String tipoServicio) {
        return switch (tipoServicio) {
            case "PlasSB" -> new PlasSBGateway(restClientBuilder.clone().baseUrl(plassbUrl).build());
            case "ContSocket" -> new ContSocketGateway(contsocketHost, contsocketPuerto);
            default -> throw new ServicioExternoException("No existe un gateway para el tipo de servicio " + tipoServicio);
        };
    }
}
