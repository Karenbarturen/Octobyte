package com.octobyte.service;

import com.octobyte.dto.OperationModeDto;
import com.octobyte.model.Operativo;
import com.octobyte.repository.OperativoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OperationModeService {

    private static final Logger log = LoggerFactory.getLogger(OperationModeService.class);
    private static final String DEFAULT_MODE = "Central Norte";

    private final OperativoRepository operativoRepository;

    public OperationModeService(OperativoRepository operativoRepository) {
        this.operativoRepository = operativoRepository;
    }

    @Transactional(readOnly = true)
    public Operativo getActiveMode() {
        return operativoRepository.findByActivoTrue()
                .orElseGet(this::createDefaultIfMissing);
    }

    @Transactional(readOnly = true)
    public Operativo findByName(String nombre) {
        return operativoRepository.findByNombre(nombre)
                .orElse(null);
    }

    @Transactional
    public Operativo create(OperationModeDto dto) {
        Operativo operativo = new Operativo();
        operativo.setNombre(dto.getNombre());
        operativo.setActivo(false);
        return operativoRepository.save(operativo);
    }

    @Transactional
    public void activate(Integer id) {
        List<Operativo> all = operativoRepository.findAll();
        for (Operativo op : all) {
            op.setActivo(op.getIdOperativo().equals(id));
        }
        operativoRepository.saveAll(all);
    }

    @Transactional(readOnly = true)
    public List<Operativo> findAll() {
        return operativoRepository.findAll();
    }

    private Operativo createDefaultIfMissing() {
        log.warn("No hay modo operativo activo, creando fallback '{}'", DEFAULT_MODE);
        Operativo operativo = new Operativo();
        operativo.setNombre(DEFAULT_MODE);
        operativo.setActivo(true);
        return operativoRepository.save(operativo);
    }
}
