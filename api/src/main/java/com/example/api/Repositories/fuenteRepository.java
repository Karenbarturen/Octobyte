package com.example.api.Repositories;

import com.example.api.Models.fuenteModel;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class fuenteRepository {

    public List<fuenteModel> findAll() {
        return Collections.emptyList();
    }

    public Optional<fuenteModel> findByNombre(String nombre) {
        return Optional.empty();
    }
}
