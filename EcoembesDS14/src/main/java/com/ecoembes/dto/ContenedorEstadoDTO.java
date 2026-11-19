package com.ecoembes.dto;

import java.time.LocalDate;

import com.ecoembes.entity.NivelLlenado;

//Estado de un contenedor de una zona en una fecha (última lectura disponible ese día)
public record ContenedorEstadoDTO(String idContenedor, String ubicacion, LocalDate fechaLectura,
                                  int numEnvases, NivelLlenado nivelLlenado) {
}
