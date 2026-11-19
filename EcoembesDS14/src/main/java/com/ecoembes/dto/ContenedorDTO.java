package com.ecoembes.dto;

import com.ecoembes.entity.NivelLlenado;

//Estado actual de un contenedor
public record ContenedorDTO(String id, String ubicacion, String codigoPostal, int capacidad,
                            int numEnvases, NivelLlenado nivelLlenado) {
}
