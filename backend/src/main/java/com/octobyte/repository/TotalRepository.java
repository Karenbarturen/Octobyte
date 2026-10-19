package com.octobyte.repository;

import com.octobyte.model.Total;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TotalRepository extends JpaRepository<Total, Integer> {
    Optional<Total> findTopByOrderByIdTotalDesc();
    List<Total> findByIdTotalLessThanOrderByIdTotalDesc(Integer cursor, Pageable pageable);
}
