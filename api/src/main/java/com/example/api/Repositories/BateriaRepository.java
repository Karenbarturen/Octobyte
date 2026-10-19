package com.example.api.Repositories;

import com.example.api.Models.BateriaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BateriaRepository extends JpaRepository<BateriaModel, Integer> {
}
