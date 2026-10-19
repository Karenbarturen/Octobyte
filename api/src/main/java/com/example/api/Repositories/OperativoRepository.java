package com.example.api.Repositories;

import com.example.api.Models.OperativoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperativoRepository extends JpaRepository<OperativoModel, Integer> {
}
