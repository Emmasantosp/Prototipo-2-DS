package com.ecoembes.gateway;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.ecoembes.exception.PeticionInvalidaException;
import com.ecoembes.exception.ServicioExternoException;

/** Service Gateway de PlasSB Ltd.: se comunica con su servidor Spring Boot por HTTP/REST. */
public class PlasSBGateway implements IPlantaGateway {

    private final RestClient restClient;

    public PlasSBGateway(RestClient restClient) {
        this.restClient = restClient;
    }

    //GET /plassb/capacidad?fecha=AAAA-MM-DD -> 200 {fecha, capacidadToneladas} | 404 sin capacidad publicada
    @Override
    public Optional<Double> consultarCapacidad(LocalDate fecha) {
        try {
            PlasSBCapacidadDTO dto = restClient.get()
                    .uri(uri -> uri.path("/plassb/capacidad").queryParam("fecha", fecha.toString()).build())
                    .retrieve()
                    .body(PlasSBCapacidadDTO.class);
            return Optional.ofNullable(dto).map(PlasSBCapacidadDTO::capacidadToneladas);
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty(); //PlasSB no ha publicado capacidad para ese día
        } catch (RestClientException e) {
            throw new ServicioExternoException("No se ha podido contactar con PlasSB: " + e.getMessage());
        }
    }

    //POST /plassb/notificaciones con body {fecha, numContenedores, totalEnvases} -> 201 | 400 datos inválidos
    @Override
    public void notificarAsignacion(LocalDate fecha, int numContenedores, int totalEnvases) {
        try {
            restClient.post()
                    .uri("/plassb/notificaciones")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new PlasSBNotificacionDTO(fecha, numContenedores, totalEnvases))
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException e) {
            throw new PeticionInvalidaException("PlasSB ha rechazado la notificación: " + e.getResponseBodyAsString());
        } catch (RestClientException e) {
            throw new ServicioExternoException("No se ha podido contactar con PlasSB: " + e.getMessage());
        }
    }
}
