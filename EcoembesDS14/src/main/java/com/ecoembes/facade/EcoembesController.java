package com.ecoembes.facade;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecoembes.dto.AsignacionDTO;
import com.ecoembes.dto.AsignacionResultadoDTO;
import com.ecoembes.dto.CapacidadPlantaDTO;
import com.ecoembes.dto.CredencialesDTO;
import com.ecoembes.dto.ErrorDTO;
import com.ecoembes.dto.PlantaDTO;
import com.ecoembes.dto.TokenDTO;
import com.ecoembes.entity.AsignacionPlanta;
import com.ecoembes.entity.CapacidadPlanta;
import com.ecoembes.entity.Empleado;
import com.ecoembes.exception.CredencialesIncorrectasException;
import com.ecoembes.service.EcoembesService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Fachada remota de Ecoembes: login/logout, plantas de reciclaje y asignaciones.
 * Cada método lleva su @Tag para que en Swagger sigan saliendo en secciones separadas.
 */
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class EcoembesController {

    private final EcoembesService ecoembesService;

    public EcoembesController(EcoembesService ecoembesService) {
        this.ecoembesService = ecoembesService;
    }

    //AUTENTICACIÓN

    @PostMapping("/auth/login")
    @Tag(name = "1. Autenticación", description = "Login y logout del personal de Ecoembes")
    @Operation(summary = "Login con email y contraseña", description = "Devuelve un token que hay que enviar en las demás peticiones.")
    @ApiResponse(responseCode = "200", description = "Login correcto")
    @ApiResponse(responseCode = "400", description = "Faltan el email o la contraseña", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    @ApiResponse(responseCode = "401", description = "Credenciales incorrectas", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    public ResponseEntity<TokenDTO> login(@Valid @RequestBody CredencialesDTO credenciales) {
        return ecoembesService.login(credenciales.email(), credenciales.password()) //se llama al service para validar las credenciales y generar un token
                .map(token -> { //si la caja tiene un token, se obtiene el empleado asociado y se devuelve un TokenDTO con el token, nombre y email
                    Empleado e = ecoembesService.getEmpleado(token);
                    return ResponseEntity.ok(new TokenDTO(token, e.getNombre(), e.getEmail()));
                })
                .orElseThrow(CredencialesIncorrectasException::new); //si la caja está vacía (no tiene token), se lanza una excepción de credenciales incorrectas
    }

    @PostMapping("/auth/logout")
    @Tag(name = "1. Autenticación")
    @Operation(summary = "Logout", description = "Elimina el token del estado del servidor: deja de ser válido.")
    @ApiResponse(responseCode = "204", description = "Sesión cerrada")
    @ApiResponse(responseCode = "401", description = "Token inválido o ya caducado", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    public ResponseEntity<Void> logout(
            @Parameter(description = "Token de sesión", required = true) @RequestParam("token") String token) {
        ecoembesService.logout(token); //elimina el token del estado del servidor para que deje de ser válido
        return ResponseEntity.noContent().build(); //si todo va bien, devuelve un 204 No Content
    }

    //PLANTAS DE RECICLAJE

    @GetMapping("/plantas")
    @Tag(name = "3. Plantas de reciclaje", description = "Plantas disponibles y su capacidad diaria")
    @Operation(summary = "Listar las plantas de reciclaje", description = "Permite al personal seleccionar la planta a consultar o a la que asignar.")
    @ApiResponse(responseCode = "200", description = "Lista de plantas")
    @ApiResponse(responseCode = "401", description = "Token inválido", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    public ResponseEntity<List<PlantaDTO>> getPlantas(
            @Parameter(description = "Token de sesión", required = true) @RequestParam("token") String token) {
        ecoembesService.getEmpleado(token);
        return ResponseEntity.ok(ecoembesService.getPlantas().stream().map(p -> new PlantaDTO(p.getId(), p.getNombre())).toList());
    }

    @GetMapping("/plantas/{idPlanta}/capacidad")
    @Tag(name = "3. Plantas de reciclaje")
    @Operation(summary = "Capacidad disponible de una planta en una fecha dada", description = "Capacidad en toneladas para el día indicado: la que publica la planta (pedida a través de su Service Gateway, PlasSB por REST o ContSocket por sockets) menos lo que Ecoembes ya le ha asignado ese día.")
    @ApiResponse(responseCode = "200", description = "Capacidad de la planta ese día")
    @ApiResponse(responseCode = "400", description = "Fecha inválida", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    @ApiResponse(responseCode = "401", description = "Token inválido", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    @ApiResponse(responseCode = "404", description = "La planta no existe o no hay capacidad publicada para esa fecha", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    @ApiResponse(responseCode = "503", description = "La planta de reciclaje no responde", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    public ResponseEntity<CapacidadPlantaDTO> consultarCapacidad(
            @Parameter(description = "Id de la planta", example = "PL-01") @PathVariable("idPlanta") String idPlanta,
            @Parameter(description = "Fecha (AAAA-MM-DD)", required = true) @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @Parameter(description = "Token de sesión", required = true) @RequestParam("token") String token) {
        ecoembesService.getEmpleado(token);
        CapacidadPlanta capacidad = ecoembesService.consultarCapacidad(idPlanta, fecha); //la planta se pide a través de su gateway
        return ResponseEntity.ok(new CapacidadPlantaDTO(capacidad.getPlanta().getId(), capacidad.getPlanta().getNombre(),
                capacidad.getFecha(), capacidad.getToneladas()));
    }

    //ASIGNACIONES

    @PostMapping("/asignaciones")
    @Tag(name = "4. Asignaciones", description = "Asignación de contenedores a plantas de reciclaje")
    @Operation(summary = "Asignar contenedores a una planta de reciclaje",
               description = "Consulta la capacidad disponible de la planta ese día, guarda la asignación en la base de datos (con la identidad de la persona que realizó la asignación) y notifica a la planta, a través de su gateway, el nº total de contenedores y de envases.")
    @ApiResponse(responseCode = "201", description = "Asignación registrada")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o capacidad insuficiente", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    @ApiResponse(responseCode = "401", description = "Token inválido", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    @ApiResponse(responseCode = "404", description = "La planta o algún contenedor no existe", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    @ApiResponse(responseCode = "503", description = "La planta de reciclaje no responde", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    public ResponseEntity<AsignacionResultadoDTO> asignar(
            @Parameter(description = "Token de sesión", required = true) @RequestParam("token") String token,
            @Valid @RequestBody AsignacionDTO dto) {
        Empleado empleado = ecoembesService.getEmpleado(token);
        AsignacionPlanta a = ecoembesService.asignar(dto.idPlanta(), dto.fecha(), dto.idsContenedores(), empleado);
        AsignacionResultadoDTO resultado = new AsignacionResultadoDTO(
                a.getId(), a.getPlanta().getId(), a.getPlanta().getNombre(), a.getFecha(),
                a.getContenedores().size(), a.getTotalEnvases(),
                ecoembesService.toneladasEstimadas(a.getTotalEnvases()),
                ecoembesService.capacidadRestante(a), empleado.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }
}
