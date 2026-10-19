package com.example.api.Repositories;

import com.example.api.Models.MedicionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicionRepository extends JpaRepository<MedicionModel, Integer> {
}
