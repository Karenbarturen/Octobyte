package com.example.api.Repositories;

import com.example.api.Models.medicionTotalModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface medicionTotalRepository extends JpaRepository<medicionTotalModel, Integer> {
}
