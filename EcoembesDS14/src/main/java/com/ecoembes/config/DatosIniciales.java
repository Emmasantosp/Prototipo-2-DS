package com.ecoembes.config;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ecoembes.dao.ContenedorRepository;
import com.ecoembes.dao.EmpleadoRepository;
import com.ecoembes.dao.PlantaRepository;
import com.ecoembes.entity.Contenedor;
import com.ecoembes.entity.Empleado;
import com.ecoembes.entity.NivelLlenado;
import com.ecoembes.entity.PlantaReciclaje;

/**
 * Carga los datos de prueba en la base de datos al arrancar, solo si las tablas están vacías.
 * En el Prototipo 1 estos datos se creaban en el constructor de cada AppService.
 */
@Component
public class DatosIniciales implements CommandLineRunner {

    private final EmpleadoRepository empleadoRepository;
    private final ContenedorRepository contenedorRepository;
    private final PlantaRepository plantaRepository;

    public DatosIniciales(EmpleadoRepository empleadoRepository, ContenedorRepository contenedorRepository,
                          PlantaRepository plantaRepository) {
        this.empleadoRepository = empleadoRepository;
        this.contenedorRepository = contenedorRepository;
        this.plantaRepository = plantaRepository;
    }

    @Override
    public void run(String... args) {
        if (empleadoRepository.count() == 0) {
            empleadoRepository.saveAll(List.of(
                    new Empleado("EMP-01", "Lorea García", "lorea@ecoembes.com", "1234"),
                    new Empleado("EMP-02", "Jon Etxeberria", "jon@ecoembes.com", "4321"),
                    new Empleado("EMP-03", "Iker López", "iker@ecoembes.com", "1122")));
        }
        if (plantaRepository.count() == 0) {
            plantaRepository.saveAll(List.of(
                    new PlantaReciclaje("PL-01", "PlasSB Ltd.", "PlasSB"),
                    new PlantaReciclaje("PL-02", "ContSocket Ltd.", "ContSocket")));
        }
        if (contenedorRepository.count() == 0) {
            contenedorRepository.saveAll(crearContenedores());
        }
    }

    private List<Contenedor> crearContenedores() {
        Object[][] datos = {
            {"CNT-BIO-001", "Gran Vía 23", "48001", 420, 110},
            {"CNT-BIO-002", "Plaza Moyúa 1", "48001", 300, 90},
            {"CNT-BIO-003", "Calle Ercilla 12", "48001", 80, 40},
            {"CNT-VIT-001", "Calle Dato 10", "01001", 500, 115},
            {"CNT-VIT-002", "Plaza de la Virgen Blanca 2", "01001", 200, 60},
            {"CNT-VIT-003", "Calle Postas 25", "01001", 60, 30},
        };
        LocalDate hoy = LocalDate.now();
        List<Contenedor> contenedores = new ArrayList<>();
        for (Object[] d : datos) {
            Contenedor c = new Contenedor((String) d[0], (String) d[1], (String) d[2], 1000);
            int base = (int) d[3], paso = (int) d[4];
            for (int dia = 4; dia >= 0; dia--) {          // lecturas de los últimos 5 días
                int envases = Math.min(1000, base + paso * (4 - dia));
                c.registrarLectura(hoy.minusDays(dia), envases, nivelSegunOcupacion(envases, 1000));
            }
            contenedores.add(c);
        }
        return contenedores;
    }

    //Solo para generar datos de prueba coherentes: el nivel real lo envía el sensor
    private static NivelLlenado nivelSegunOcupacion(int envases, int capacidad) {
        double ocupacion = (double) envases / capacidad;
        if (ocupacion < 0.5) return NivelLlenado.VERDE;
        if (ocupacion < 0.8) return NivelLlenado.NARANJA;
        return NivelLlenado.ROJO;
    }
}
