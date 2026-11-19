package com.ecoembes.dto;

import java.time.LocalDate;
import java.util.List;

//Uso/estado de un contenedor entre dos fechas
public record ContenedorHistorialDTO(String idContenedor, String ubicacion, String codigoPostal,
                                     LocalDate inicio, LocalDate fin, List<RegistroUsoDTO> registros) {
}
