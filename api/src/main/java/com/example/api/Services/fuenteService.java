package com.example.api.Services;

import com.example.api.Models.fuenteModel;
import com.example.api.Repositories.fuenteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class fuenteService {

    private final fuenteRepository fuenteRepository;

    public fuenteService(fuenteRepository fuenteRepository) {
        this.fuenteRepository = fuenteRepository;
    }

    public List<fuenteModel> getAllFuentes() {
        return fuenteRepository.findAll();
    }
}
