package com.plassb.config;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.plassb.dao.CapacidadRepository;
import com.plassb.entity.CapacidadDiaria;

/** Publica la capacidad de hoy y de los 10 días siguientes (solo los días que aún no estén en la BD). */
@Component
public class DatosIniciales implements CommandLineRunner {

    private final CapacidadRepository capacidadRepository;

    public DatosIniciales(CapacidadRepository capacidadRepository) {
        this.capacidadRepository = capacidadRepository;
    }

    @Override
    public void run(String... args) {
        LocalDate hoy = LocalDate.now();
        for (int d = 0; d <= 10; d++) {
            LocalDate fecha = hoy.plusDays(d);
            if (!capacidadRepository.existsById(fecha)) {
                capacidadRepository.save(new CapacidadDiaria(fecha, 20.0 + Math.floorMod(d, 5) * 2.5));
            }
        }
    }
}
