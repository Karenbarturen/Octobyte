package com.octobyte.repository;

import com.octobyte.model.Electricidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElectricidadRepository extends JpaRepository<Electricidad, Integer> {
}
