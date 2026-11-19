package com.ecoembes.facade;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecoembes.dto.CredencialesDTO;
import com.ecoembes.dto.TokenDTO;
import com.ecoembes.entity.Empleado;
import com.ecoembes.exception.CredencialesIncorrectasException;
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

/** Fachada remota: autenticación del personal de Ecoembes */
@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "1. Autenticación", description = "Login y logout del personal de Ecoembes")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Login con email y contraseña", description = "Devuelve un token que hay que enviar en las demás peticiones.")
    @ApiResponse(responseCode = "200", description = "Login correcto")
    @ApiResponse(responseCode = "400", description = "Faltan el email o la contraseña", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    @ApiResponse(responseCode = "401", description = "Credenciales incorrectas", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    public ResponseEntity<TokenDTO> login(@Valid @RequestBody CredencialesDTO credenciales) {
        return authService.login(credenciales.email(), credenciales.password()) //se llama a AuthService para validar las credenciales y generar un token
                .map(token -> { //si la caja tiene un token, se obtiene el empleado asociado al token y se devuelve un TokenDTO con el token, nombre y email del empleado
                    Empleado e = authService.getEmpleado(token);
                    return ResponseEntity.ok(new TokenDTO(token, e.getNombre(), e.getEmail()));
                })
                .orElseThrow(CredencialesIncorrectasException::new); //si la caja está vacía (no tiene token), se lanza una excepción de credenciales incorrectas
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Elimina el token del estado del servidor: deja de ser válido.")
    @ApiResponse(responseCode = "204", description = "Sesión cerrada")
    @ApiResponse(responseCode = "401", description = "Token inválido o ya caducado", content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
    public ResponseEntity<Void> logout(
            @Parameter(description = "Token de sesión", required = true) @RequestParam("token") String token) {
        authService.logout(token); //llama a AuthService para eliminar el token del estado del servidor para que deje de ser válido
        return ResponseEntity.noContent().build(); //si todo va bien, devuelve un 204 No Content indicando que la sesión se ha cerrado correctamente
    }
}
