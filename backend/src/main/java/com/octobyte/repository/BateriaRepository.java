package com.octobyte.repository;

import com.octobyte.model.Bateria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BateriaRepository extends JpaRepository<Bateria, Integer> {
}
