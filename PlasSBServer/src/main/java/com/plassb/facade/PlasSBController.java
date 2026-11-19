package com.plassb.facade;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.plassb.dto.CapacidadDTO;
import com.plassb.dto.NotificacionDTO;
import com.plassb.dto.NotificacionRecibidaDTO;
import com.plassb.entity.Notificacion;
import com.plassb.service.PlasSBService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/** Fachada remota de PlasSB: la usa el PlasSBGateway del Servidor Ecoembes. */
@RestController
@RequestMapping("/api")
@Tag(name = "PlasSB", description = "Capacidad diaria y notificaciones de asignación")
public class PlasSBController {

    private final PlasSBService plasSBService;

    public PlasSBController(PlasSBService plasSBService) {
        this.plasSBService = plasSBService;
    }

    @GetMapping("/capacidad")
    @Operation(summary = "Capacidad disponible (toneladas) para una fecha")
    @ApiResponse(responseCode = "200", description = "Capacidad disponible ese día")
    @ApiResponse(responseCode = "404", description = "No hay capacidad publicada para esa fecha")
    public ResponseEntity<CapacidadDTO> consultarCapacidad(
            @Parameter(description = "Fecha (AAAA-MM-DD)", required = true) @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return plasSBService.consultarCapacidad(fecha)
                .map(c -> ResponseEntity.ok(new CapacidadDTO(c.getFecha(), c.getDisponible())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/notificaciones")
    @Operation(summary = "Recibir la notificación de una asignación",
               description = "Registra los contenedores y envases que llegarán a la planta y descuenta las toneladas de la capacidad de ese día.")
    @ApiResponse(responseCode = "201", description = "Notificación registrada")
    @ApiResponse(responseCode = "400", description = "Datos inválidos, sin capacidad publicada o capacidad insuficiente")
    public ResponseEntity<?> recibirNotificacion(@Valid @RequestBody NotificacionDTO dto) {
        try {
            Notificacion n = plasSBService.registrarNotificacion(dto.fecha(), dto.numContenedores(), dto.totalEnvases(), dto.toneladas());
            return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(n));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/notificaciones")
    @Operation(summary = "Notificaciones recibidas (de la más reciente a la más antigua)")
    @ApiResponse(responseCode = "200", description = "Lista de notificaciones")
    public ResponseEntity<List<NotificacionRecibidaDTO>> getNotificaciones() {
        return ResponseEntity.ok(plasSBService.getNotificaciones().stream().map(this::toDTO).toList());
    }

    private NotificacionRecibidaDTO toDTO(Notificacion n) {
        return new NotificacionRecibidaDTO(n.getId(), n.getFecha(), n.getNumContenedores(), n.getTotalEnvases(),
                n.getToneladas(), n.getFechaHoraRecepcion());
    }
}
