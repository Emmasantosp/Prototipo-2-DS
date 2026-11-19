package com.ecoembes.dto;

import java.time.LocalDate;

//Resultado de una asignación, incluida la información de auditoría
public record AsignacionResultadoDTO(String idAsignacion, String idPlanta, String nombrePlanta, LocalDate fecha,
                                     int numContenedores, int totalEnvases, double toneladasEstimadas,
                                     double capacidadRestanteToneladas, String asignadoPor) {
}
