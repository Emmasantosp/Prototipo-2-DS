package com.ecoembes.facade;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecoembes.dto.AsignacionDTO;
import com.ecoembes.dto.AsignacionResultadoDTO;
import com.ecoembes.entity.AsignacionPlanta;
import com.ecoembes.entity.Empleado;
import com.ecoembes.service.AsignacionService;
import com.ecoembes.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import com.ecoembes.dto.ErrorDTO;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

/** Fachada remota: asignación de contenedores a plantas */
@RestController
@RequestMapping(value = "/asignaciones", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "4. Asignaciones", description = "Asignación de contenedores a plantas de reciclaje")
public class AsignacionController {

    private final AuthService authService;
    private final AsignacionService asignacionService;

    public AsignacionController(AuthService authService, AsignacionService asignacionService) {
        this.authService = authService;
        this.asignacionService = asignacionService;
    }

    @PostMapping
    @Operation(summary = "Asignar contenedores a una planta de reciclaje",
               description = "Consulta la capacidad disponible de la planta ese día a través de su gateway, notifica a la planta y guarda la asignación en la base de datos (con la identidad de la persona que realizó la asignación).")
    @ApiResponse(responseCode = "201", description = "Asignación registrada")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o capacidad insuficiente",
    content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    @ApiResponse(responseCode = "401", description = "Token inválido", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    @ApiResponse(responseCode = "404", description = "La planta o algún contenedor no existe", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    @ApiResponse(responseCode = "503", description = "La planta de reciclaje no responde", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    public ResponseEntity<AsignacionResultadoDTO> asignar(
            @Parameter(description = "Token de sesión", required = true) @RequestParam("token") String token,
            @Valid @RequestBody AsignacionDTO dto) {
        Empleado empleado = authService.getEmpleado(token);
        AsignacionPlanta a = asignacionService.asignar(dto.idPlanta(), dto.fecha(), dto.idsContenedores(), empleado);
        AsignacionResultadoDTO resultado = new AsignacionResultadoDTO(
                a.getId(), a.getPlanta().getId(), a.getPlanta().getNombre(), a.getFecha(),
                a.getContenedores().size(), a.getTotalEnvases(),
                asignacionService.toneladasEstimadas(a.getTotalEnvases()),
                asignacionService.capacidadRestante(a), empleado.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }
}
