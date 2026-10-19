package com.example.api.Repositories;

import com.example.api.Models.ElectricidadModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElectricidadRepository extends JpaRepository<ElectricidadModel, Integer> {
}
