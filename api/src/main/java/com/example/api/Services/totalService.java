package com.example.api.Services;

import com.example.api.DTO.RegistroCompletoRequestDTO;
import com.example.api.DTO.RegistroCompletoResponseDTO;
import com.example.api.DTO.totalRequestDTO;
import com.example.api.DTO.ultimoTotalDTO;
import com.example.api.Models.*;
import com.example.api.Repositories.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class totalService {

    private final totalRepository totalRepository;
    private final OperativoRepository operativoRepository;
    private final BateriaRepository bateriaRepository;
    private final ElectricidadRepository electricidadRepository;
    private final MedicionRepository medicionRepository;
    private final medicionTotalRepository medicionTotalRepository;

    public totalService(totalRepository totalRepository,
                       OperativoRepository operativoRepository,
                       BateriaRepository bateriaRepository,
                       ElectricidadRepository electricidadRepository,
                       MedicionRepository medicionRepository,
                       medicionTotalRepository medicionTotalRepository) {
        this.totalRepository = totalRepository;
        this.operativoRepository = operativoRepository;
        this.bateriaRepository = bateriaRepository;
        this.electricidadRepository = electricidadRepository;
        this.medicionRepository = medicionRepository;
        this.medicionTotalRepository = medicionTotalRepository;
    }

    public List<totalModel> getAllTotales() {
        return totalRepository.findAll();
    }

    public ultimoTotalDTO getUltimoTotal() {
        return totalRepository.findTopByOrderByIdTotalDesc()
                .map(total -> {
                    Integer idTotal = total.getIdTotal();
                    Integer numAlerta = total.getNumAlerta() != null ? total.getNumAlerta() : 0;
                    LocalDateTime fecha = total.getFecha() != null ? total.getFecha() : LocalDateTime.now();
                    Integer idMedicionTotal = total.getIdMedicionTotal() != null ? total.getIdMedicionTotal() : 0;

                    return new ultimoTotalDTO(
                            idTotal,
                            0L,
                            numAlerta,
                            fecha,
                            idMedicionTotal,
                            0,
                            "",
                            "TOTAL-" + idTotal,
                            "Registro de total normalizado",
                            "kWh",
                            numAlerta > 0 ? "ALERTA" : "OK",
                            "TOTAL",
                            "Valor preparado para procesamiento posterior",
                            numAlerta > 0,
                            "1.0.0"
                    );
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existen registros en Total"));
    }

    public totalModel createTotal(totalRequestDTO request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La solicitud es obligatoria");
        }

        if (request.getIdMedicionTotal() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El idMedicionTotal es obligatorio");
        }

        totalModel total = new totalModel();
        total.setIdMedicionTotal(request.getIdMedicionTotal());
        total.setNumAlerta(request.getNumAlerta() != null ? request.getNumAlerta() : 0);
        total.setFecha(request.getFecha() != null ? request.getFecha() : LocalDateTime.now());

        return totalRepository.save(total);
    }

    @Transactional
    public RegistroCompletoResponseDTO createCompleteRecord(RegistroCompletoRequestDTO request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La solicitud es obligatoria");
        }

        OperativoModel operativo = new OperativoModel();
        operativo.setNombre(request.getNombre() != null ? request.getNombre() : "Operativo");
        OperativoModel savedOperativo = operativoRepository.save(operativo);

        BateriaModel bateria = new BateriaModel();
        bateria.setEnergiaGuardada(request.getEnergiaGuardada() != null ? request.getEnergiaGuardada() : 0);
        bateria.setNivelPorcentaje(request.getNivelPorcentaje() != null ? request.getNivelPorcentaje() : 0);
        bateria.setIdOperativo(savedOperativo.getIdOperativo());
        BateriaModel savedBateria = bateriaRepository.save(bateria);

        ElectricidadModel electricidad = new ElectricidadModel();
        electricidad.setVoltaje(request.getVoltaje() != null ? request.getVoltaje() : 0);
        electricidad.setCorriente(request.getCorriente() != null ? request.getCorriente() : 0);
        electricidad.setGeneracionElectricidad(request.getGeneracionElectricidad() != null ? request.getGeneracionElectricidad() : 0);
        electricidad.setConsumoElectricidad(request.getConsumoElectricidad() != null ? request.getConsumoElectricidad() : 0);
        ElectricidadModel savedElectricidad = electricidadRepository.save(electricidad);

        MedicionModel medicion = new MedicionModel();
        medicion.setViento(request.getViento() != null ? request.getViento() : 0);
        medicion.setTemperatura(request.getTemperatura() != null ? request.getTemperatura() : 0);
        medicion.setLumens(request.getLumens() != null ? request.getLumens() : 0);
        MedicionModel savedMedicion = medicionRepository.save(medicion);

        medicionTotalModel medicionTotal = new medicionTotalModel();
        medicionTotal.setIdBateria(savedBateria.getIdBateria());
        medicionTotal.setIdElectricidad(savedElectricidad.getIdElectricidad());
        medicionTotal.setIdMedicion(savedMedicion.getIdMedicion());
        medicionTotalModel savedMedicionTotal = medicionTotalRepository.save(medicionTotal);

        totalModel total = new totalModel();
        total.setIdMedicionTotal(savedMedicionTotal.getIdMedicionTotal());
        total.setNumAlerta(request.getNumAlerta() != null ? request.getNumAlerta() : 0);
        total.setFecha(LocalDateTime.now());
        totalModel savedTotal = totalRepository.save(total);

        return new RegistroCompletoResponseDTO(
                savedTotal.getFecha(),
                savedTotal.getNumAlerta(),
                savedOperativo.getNombre(),
                savedBateria.getEnergiaGuardada(),
                savedBateria.getNivelPorcentaje(),
                savedElectricidad.getVoltaje(),
                savedElectricidad.getCorriente(),
                savedElectricidad.getGeneracionElectricidad(),
                savedElectricidad.getConsumoElectricidad(),
                savedMedicion.getViento(),
                savedMedicion.getTemperatura(),
                savedMedicion.getLumens()
        );
    }

    public RegistroCompletoResponseDTO getUltimoRegistroCompleto() {
        return totalRepository.findUltimosRegistrosCompleto().stream()
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existen registros completos"));
    }
}
