package com.ecoembes.facade;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecoembes.dto.ActualizarContenedorDTO;
import com.ecoembes.dto.ContenedorDTO;
import com.ecoembes.dto.ContenedorEstadoDTO;
import com.ecoembes.dto.ContenedorHistorialDTO;
import com.ecoembes.dto.CrearContenedorDTO;
import com.ecoembes.dto.RegistroUsoDTO;
import com.ecoembes.entity.Contenedor;
import com.ecoembes.service.EcoembesService;
import com.ecoembes.service.ContenedorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import com.ecoembes.dto.ErrorDTO;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

/** Fachada remota: gestión de contenedores */
@RestController
@RequestMapping(value = "/contenedores", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "2. Contenedores", description = "creación de contenedores, lecturas del sensor RecycloBin y consultas de los contenedores")
public class ContenedorController {

    private final EcoembesService ecoembesService;
    private final ContenedorService contenedorService;

    public ContenedorController(EcoembesService ecoembesService, ContenedorService contenedorService) {
        this.ecoembesService = ecoembesService;
        this.contenedorService = contenedorService;
    }

    @PostMapping
    @Operation(summary = "Crear un contenedor", description = "Crea un contenedor con su id, ubicación, código postal y capacidad inicial.")
    @ApiResponse(responseCode = "201", description = "Contenedor creado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o id ya existente", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    @ApiResponse(responseCode = "401", description = "Token inválido", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    public ResponseEntity<ContenedorDTO> crearContenedor(
            @Parameter(description = "Token de sesión", required = true) @RequestParam("token") String token,
            @Valid @RequestBody CrearContenedorDTO dto) {
        ecoembesService.getEmpleado(token); //se llama a ecoembesService para validar el token y obtener el empleado asociado, si el token es inválido se lanza una excepción
        Contenedor c = contenedorService.crearContenedor(dto.id(), dto.ubicacion(), dto.codigoPostal(), dto.capacidad()); //si todo va bien, se llama a ContenedorService para crear el contenedor con los datos del DTO y se devuelve el contenedor creado
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(c));
    }

    @PutMapping("/{idContenedor}")
    @Operation(summary = "Actualizar un contenedor (lectura del sensor)",
               description = "Simula el envío diario del sensor con el nº estimado de envases. Se guarda como lectura de hoy y el nivel de llenado se calcula según la ocupación.")
    @ApiResponse(responseCode = "200", description = "Contenedor actualizado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    @ApiResponse(responseCode = "401", description = "Token inválido", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    @ApiResponse(responseCode = "404", description = "El contenedor no existe", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    public ResponseEntity<ContenedorDTO> actualizarContenedor(
            @Parameter(description = "Id del contenedor", example = "CNT-BIO-001") @PathVariable("idContenedor") String idContenedor,
            @Parameter(description = "Token de sesión", required = true) @RequestParam("token") String token,
            @Valid @RequestBody ActualizarContenedorDTO dto) {
        ecoembesService.getEmpleado(token);
        Contenedor c = contenedorService.actualizarContenedor(idContenedor, dto.numEnvases());
        return ResponseEntity.ok(toDTO(c));
    }

    @GetMapping("/{idContenedor}/uso")
    @Operation(summary = "Uso/estado de un contenedor entre una fecha de inicio y una de fin")
    @ApiResponse(responseCode = "200", description = "Histórico de lecturas del periodo")
    @ApiResponse(responseCode = "400", description = "Fechas inválidas", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    @ApiResponse(responseCode = "401", description = "Token inválido", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    @ApiResponse(responseCode = "404", description = "El contenedor no existe", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    public ResponseEntity<ContenedorHistorialDTO> consultarUso(
            @Parameter(description = "Id del contenedor", example = "CNT-BIO-001") @PathVariable("idContenedor") String idContenedor,
            @Parameter(description = "Fecha de inicio (AAAA-MM-DD)", required = true) @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @Parameter(description = "Fecha de fin (AAAA-MM-DD)", required = true) @RequestParam("fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
            @Parameter(description = "Token de sesión", required = true) @RequestParam("token") String token) {
        ecoembesService.getEmpleado(token);
        Contenedor c = contenedorService.getContenedor(idContenedor);
        List<RegistroUsoDTO> registros = contenedorService.consultarUso(idContenedor, inicio, fin).stream()
                .map(r -> new RegistroUsoDTO(r.getFecha(), r.getNumEnvases(), r.getNivelLlenado()))
                .toList();
        return ResponseEntity.ok(new ContenedorHistorialDTO(c.getId(), c.getUbicacion(), c.getCodigoPostal(), inicio, fin, registros));
    }

    @GetMapping
    @Operation(summary = "Estado de los contenedores de una zona en una fecha",
               description = "La zona se identifica por código postal. Para cada contenedor se devuelve la última lectura disponible ese día.")
    @ApiResponse(responseCode = "200", description = "Lista de contenedores de la zona (puede estar vacía)")
    @ApiResponse(responseCode = "400", description = "Parámetros inválidos", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    @ApiResponse(responseCode = "401", description = "Token inválido", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    public ResponseEntity<List<ContenedorEstadoDTO>> consultarZona( //devuelve una lista de contenedores de la zona (puede estar vacía) con su estado en la fecha indicada
            @Parameter(description = "Código postal de la zona", required = true, example = "48001") @RequestParam("codigoPostal") String codigoPostal,
            @Parameter(description = "Fecha (AAAA-MM-DD)", required = true) @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @Parameter(description = "Token de sesión", required = true) @RequestParam("token") String token) {
        ecoembesService.getEmpleado(token);
        List<ContenedorEstadoDTO> estados = contenedorService.consultarZona(codigoPostal, fecha).stream()
                .map(c -> contenedorService.getEstadoEnFecha(c, fecha) //recorre la lista de contenedores de la zona y para cada contenedor obtiene su estado en la fecha indicada, si no hay estado disponible lanza una excepción
                        .map(r -> new ContenedorEstadoDTO(c.getId(), c.getUbicacion(), r.getFecha(), r.getNumEnvases(), r.getNivelLlenado()))
                        .orElseThrow())
                .toList(); //los pone en una lista de ContenedorEstadoDTO y la devuelve
        return ResponseEntity.ok(estados);
    }

    private ContenedorDTO toDTO(Contenedor c) {
        return new ContenedorDTO(c.getId(), c.getUbicacion(), c.getCodigoPostal(), c.getCapacidad(), c.getNumEnvases(), c.getNivelLlenado());
    }
}
