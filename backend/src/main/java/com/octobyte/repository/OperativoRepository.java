package com.octobyte.repository;

import com.octobyte.model.Operativo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OperativoRepository extends JpaRepository<Operativo, Integer> {
    Optional<Operativo> findByActivoTrue();
    Optional<Operativo> findByNombre(String nombre);
}
