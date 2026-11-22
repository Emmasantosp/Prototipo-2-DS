package com.ecoembes.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecoembes.entity.PlantaReciclaje;

/** DAO de plantas de reciclaje. */
public interface PlantaRepository extends JpaRepository<PlantaReciclaje, String> {

    List<PlantaReciclaje> findAllByOrderByIdAsc();
}
