package com.octobyte.repository;

import com.octobyte.model.MedicionTotal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicionTotalRepository extends JpaRepository<MedicionTotal, Integer> {
}
