package com.plassb.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

//Notificación ya registrada en la planta
public record NotificacionRecibidaDTO(Long id, LocalDate fecha, int numContenedores, int totalEnvases,
                                      double toneladas, LocalDateTime fechaHoraRecepcion) {
}
