package com.ecoembes.facade;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecoembes.dto.CapacidadPlantaDTO;
import com.ecoembes.dto.PlantaDTO;
import com.ecoembes.entity.CapacidadPlanta;
import com.ecoembes.entity.PlantaReciclaje;
import com.ecoembes.service.AuthService;
import com.ecoembes.service.PlantaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import com.ecoembes.dto.ErrorDTO;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

/** Fachada remota: plantas de reciclaje. */
@RestController
@RequestMapping(value = "/plantas", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "3. Plantas de reciclaje", description = "Plantas disponibles y su capacidad diaria")
public class PlantaController {

    private final AuthService authService;
    private final PlantaService plantaService;

    public PlantaController(AuthService authService, PlantaService plantaService) {
        this.authService = authService;
        this.plantaService = plantaService;
    }

    @GetMapping
    @Operation(summary = "Listar las plantas de reciclaje", description = "Permite al personal seleccionar la planta a consultar o a la que asignar.")
    @ApiResponse(responseCode = "200", description = "Lista de plantas")
    @ApiResponse(responseCode = "401", description = "Token inválido", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    public ResponseEntity<List<PlantaDTO>> getPlantas(
            @Parameter(description = "Token de sesión", required = true) @RequestParam("token") String token) {
        authService.getEmpleado(token);
        return ResponseEntity.ok(plantaService.getPlantas().stream().map(p -> new PlantaDTO(p.getId(), p.getNombre())).toList());
    }

    @GetMapping("/{idPlanta}/capacidad")
    @Operation(summary = "Capacidad disponible de una planta en una fecha dada", description = "Capacidad en toneladas para el día indicado. Se pide a la planta a través de su Service Gateway (PlasSB por REST, ContSocket por sockets).")
    @ApiResponse(responseCode = "200", description = "Capacidad de la planta ese día")
    @ApiResponse(responseCode = "400", description = "Fecha inválida",
    content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    @ApiResponse(responseCode = "401", description = "Token inválido", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    @ApiResponse(responseCode = "404", description = "La planta no existe o no hay capacidad publicada para esa fecha", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    @ApiResponse(responseCode = "503", description = "La planta de reciclaje no responde", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    public ResponseEntity<CapacidadPlantaDTO> consultarCapacidad(
            @Parameter(description = "Id de la planta", example = "PL-01") @PathVariable("idPlanta") String idPlanta,
            @Parameter(description = "Fecha (AAAA-MM-DD)", required = true) @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @Parameter(description = "Token de sesión", required = true) @RequestParam("token") String token) {
        authService.getEmpleado(token);
        PlantaReciclaje planta = plantaService.getPlanta(idPlanta);
        CapacidadPlanta capacidad = plantaService.consultarCapacidad(idPlanta, fecha);
        return ResponseEntity.ok(new CapacidadPlantaDTO(planta.getId(), planta.getNombre(), fecha, capacidad.getToneladas()));
    }
}
