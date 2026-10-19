package com.octobyte.repository;

import com.octobyte.model.Medicion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicionRepository extends JpaRepository<Medicion, Integer> {
}
