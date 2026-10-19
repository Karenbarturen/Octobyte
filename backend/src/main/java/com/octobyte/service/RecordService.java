package com.octobyte.service;

import com.octobyte.dto.RecordRequestDto;
import com.octobyte.model.*;
import com.octobyte.repository.*;
import com.octobyte.util.AlertEvaluator;
import com.octobyte.util.ParsedData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class RecordService {

    private static final Logger log = LoggerFactory.getLogger(RecordService.class);

    private final BateriaRepository bateriaRepository;
    private final ElectricidadRepository electricidadRepository;
    private final MedicionRepository medicionRepository;
    private final MedicionTotalRepository medicionTotalRepository;
    private final TotalRepository totalRepository;
    private final OperationModeService operationModeService;

    private final AtomicReference<LocalDateTime> lastRecordTime = new AtomicReference<>(LocalDateTime.now());

    public RecordService(BateriaRepository bateriaRepository,
                         ElectricidadRepository electricidadRepository,
                         MedicionRepository medicionRepository,
                         MedicionTotalRepository medicionTotalRepository,
                         TotalRepository totalRepository,
                         OperationModeService operationModeService) {
        this.bateriaRepository = bateriaRepository;
        this.electricidadRepository = electricidadRepository;
        this.medicionRepository = medicionRepository;
        this.medicionTotalRepository = medicionTotalRepository;
        this.totalRepository = totalRepository;
        this.operationModeService = operationModeService;
    }

    @Transactional
    public Total createFromEsp32(ParsedData data) {
        Operativo operativo = operationModeService.getActiveMode();

        Integer lumens = data.getLumens() != null ? data.getLumens() : random(0, 4095);
        Integer windSpeed = data.getWindSpeed() != null ? data.getWindSpeed() : random(0, 30);
        Integer temperature = data.getTemperature() != null ? data.getTemperature() : random(15, 35);
        Integer voltage = data.getVoltage() != null ? data.getVoltage() : random(0, 24);

        return persistRecord(operativo, lumens, windSpeed, temperature, voltage);
    }

    @Transactional
    public Total createFromDto(RecordRequestDto dto) {
        Operativo operativo = operationModeService.findByName(dto.getOperationMode());
        if (operativo == null) {
            throw new IllegalArgumentException("Modo operativo no encontrado: " + dto.getOperationMode());
        }

        return persistRecord(
                operativo,
                dto.getLumens(),
                dto.getWindSpeed(),
                dto.getTemperature(),
                dto.getVoltage(),
                dto.getStoredEnergy(),
                dto.getBatteryLevel(),
                dto.getCurrent(),
                dto.getGenerationPower(),
                dto.getConsumptionPower(),
                dto.getAlertLevel()
        );
    }

    @Transactional
    public Total createSimulated() {
        Operativo operativo = operationModeService.getActiveMode();
        Integer lumens = random(0, 4095);
        Integer windSpeed = random(0, 30);
        Integer temperature = random(15, 35);
        Integer voltage = random(10, 24);

        return persistRecord(operativo, lumens, windSpeed, temperature, voltage);
    }

    private Total persistRecord(Operativo operativo, Integer lumens, Integer windSpeed,
                                Integer temperature, Integer voltage) {
        Integer current = random(0, 15);
        Integer generationPower = voltage * current;
        Integer consumptionPower = (int) (generationPower * ThreadLocalRandom.current().nextDouble(0.5, 0.95));
        Integer storedEnergy = random(0, 100);
        Integer batteryLevel = random(0, 100);
        Integer alertLevel = AlertEvaluator.evaluate(voltage, temperature, batteryLevel);

        return persistRecord(operativo, lumens, windSpeed, temperature, voltage,
                storedEnergy, batteryLevel, current, generationPower, consumptionPower, alertLevel);
    }

    private Total persistRecord(Operativo operativo, Integer lumens, Integer windSpeed,
                                Integer temperature, Integer voltage, Integer storedEnergy,
                                Integer batteryLevel, Integer current, Integer generationPower,
                                Integer consumptionPower, Integer alertLevel) {

        Bateria bateria = new Bateria();
        bateria.setEnergiaGuardada(storedEnergy);
        bateria.setNivelPorcentaje(batteryLevel);
        bateria.setOperativo(operativo);
        bateria = bateriaRepository.save(bateria);

        Electricidad electricidad = new Electricidad();
        electricidad.setVoltaje(voltage);
        electricidad.setCorriente(current);
        electricidad.setGeneracionElectricidad(generationPower);
        electricidad.setConsumoElectricidad(consumptionPower);
        electricidad = electricidadRepository.save(electricidad);

        Medicion medicion = new Medicion();
        medicion.setViento(windSpeed);
        medicion.setTemperatura(temperature);
        medicion.setLumens(lumens);
        medicion = medicionRepository.save(medicion);

        MedicionTotal medicionTotal = new MedicionTotal();
        medicionTotal.setBateria(bateria);
        medicionTotal.setElectricidad(electricidad);
        medicionTotal.setMedicion(medicion);
        medicionTotal = medicionTotalRepository.save(medicionTotal);

        Total total = new Total();
        total.setFecha(LocalDateTime.now());
        total.setNumAlerta(alertLevel);
        total.setMedicionTotal(medicionTotal);
        total = totalRepository.save(total);

        lastRecordTime.set(LocalDateTime.now());
        log.info("Registro guardado: idTotal={}, modo={}, alerta={}", total.getIdTotal(), operativo.getNombre(), alertLevel);

        return total;
    }

    public LocalDateTime getLastRecordTime() {
        return lastRecordTime.get();
    }

    private int random(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}
