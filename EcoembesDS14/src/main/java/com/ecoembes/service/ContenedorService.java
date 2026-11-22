package com.ecoembes.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecoembes.dao.ContenedorRepository;
import com.ecoembes.entity.Contenedor;
import com.ecoembes.entity.NivelLlenado;
import com.ecoembes.entity.RegistroUsoContenedor;
import com.ecoembes.exception.PeticionInvalidaException;
import com.ecoembes.exception.RecursoNoEncontradoException;

/** AppService de contenedores: creación de contenedores, lecturas del sensor y consultas. */
@Service
public class ContenedorService {

    //Desde el Prototipo 2 los contenedores se guardan en la base de datos (DAO)
    private final ContenedorRepository contenedorRepository;

    public ContenedorService(ContenedorRepository contenedorRepository) {
        this.contenedorRepository = contenedorRepository;
    }

    @Transactional
    public Contenedor crearContenedor(String id, String ubicacion, String codigoPostal, int capacidad) {
        if (contenedorRepository.existsById(id)) {
            throw new PeticionInvalidaException("Ya existe un contenedor con id " + id);
        }
        Contenedor c = new Contenedor(id, ubicacion, codigoPostal, capacidad);
        c.registrarLectura(LocalDate.now(), 0, NivelLlenado.VERDE); //estado inicial: vacío
        return contenedorRepository.save(c);
    }

    //Actualiza el contenedor con la lectura diaria del sensor (simulada desde Swagger/Postman en los prototipos).
    //Solo llega el nº de envases: la lectura es la de hoy y el nivel se calcula según la ocupación
    @Transactional
    public Contenedor actualizarContenedor(String id, int numEnvases) {
        Contenedor c = getContenedor(id); //busca el contenedor en la BD. Si no existe lanza 404
        if (numEnvases > c.getCapacidad()) {
            throw new PeticionInvalidaException("El nº de envases supera la capacidad del contenedor (" + c.getCapacidad() + ")");
        }
        NivelLlenado nivel = nivelSegunOcupacion(numEnvases, c.getCapacidad()); //calcula el nivel según lo lleno que está
        c.registrarLectura(LocalDate.now(), numEnvases, nivel); //guarda la lectura de hoy; si ya había una de hoy, la reemplaza
        return contenedorRepository.save(c);
    }

    @Transactional(readOnly = true)
    public Contenedor getContenedor(String id) { //busca el contenedor por id en la BD. Si no existe lanza 404
        return contenedorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el contenedor " + id));
    }

    //Uso/estado de un contenedor entre una fecha de inicio y una de fin
    @Transactional(readOnly = true)
    public List<RegistroUsoContenedor> consultarUso(String id, LocalDate inicio, LocalDate fin) {
        if (inicio.isAfter(fin)) {
            throw new PeticionInvalidaException("La fecha de inicio no puede ser posterior a la de fin");
        }
        return getContenedor(id).getHistorial(inicio, fin);
    }

    //Busca los contenedores de una zona (código postal) que ya existían en la fecha pedida, y los devuelve ordenados por id
    @Transactional(readOnly = true)
    public List<Contenedor> consultarZona(String codigoPostal, LocalDate fecha) {
        return contenedorRepository.findByCodigoPostalOrderByIdAsc(codigoPostal).stream()
                .filter(c -> c.getEstadoEnFecha(fecha).isPresent())
                .toList();
    }

    public Optional<RegistroUsoContenedor> getEstadoEnFecha(Contenedor c, LocalDate fecha) {
        return c.getEstadoEnFecha(fecha);
    }

    //Nivel de llenado según la ocupación: VERDE < 50 %, NARANJA < 80 %, ROJO el resto
    public static NivelLlenado nivelSegunOcupacion(int envases, int capacidad) {
        double ocupacion = (double) envases / capacidad;
        if (ocupacion < 0.5) return NivelLlenado.VERDE;
        if (ocupacion < 0.8) return NivelLlenado.NARANJA;
        return NivelLlenado.ROJO;
    }
}
