package com.example.api.Services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import com.example.api.DTO.RegistroCompletoDataDTO;
import com.example.api.DTO.RegistroCompletoMetadataDTO;
import com.example.api.DTO.RegistroCompletoRequestDTO;
import com.example.api.DTO.RegistroCompletoResponseDTO;
import com.example.api.Models.*;
import com.example.api.Repositories.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class totalServiceTest {

    @Mock
    private totalRepository totalRepository;

    @Mock
    private OperativoRepository operativoRepository;

    @Mock
    private BateriaRepository bateriaRepository;

    @Mock
    private ElectricidadRepository electricidadRepository;

    @Mock
    private MedicionRepository medicionRepository;

    @Mock
    private medicionTotalRepository medicionTotalRepository;

    @InjectMocks
    private totalService totalService;

    @Test
    void shouldPersistCompleteMeasurementPayloadAcrossTables() {
        RegistroCompletoRequestDTO request = new RegistroCompletoRequestDTO();
        request.setNombre("Central Norte");
        request.setEnergiaGuardada(85);
        request.setNivelPorcentaje(76);
        request.setVoltaje(230);
        request.setCorriente(8);
        request.setGeneracionElectricidad(1800);
        request.setConsumoElectricidad(1400);
        request.setViento(12);
        request.setTemperatura(24);
        request.setLumens(5000);
        request.setNumAlerta(2);

        OperativoModel operativo = new OperativoModel();
        operativo.setIdOperativo(10);
        operativo.setNombre("Central Norte");
        when(operativoRepository.save(any(OperativoModel.class))).thenReturn(operativo);

        BateriaModel bateria = new BateriaModel();
        bateria.setIdBateria(11);
        bateria.setEnergiaGuardada(85);
        bateria.setNivelPorcentaje(76);
        bateria.setIdOperativo(10);
        when(bateriaRepository.save(any(BateriaModel.class))).thenReturn(bateria);

        ElectricidadModel electricidad = new ElectricidadModel();
        electricidad.setIdElectricidad(12);
        electricidad.setVoltaje(230);
        electricidad.setCorriente(8);
        electricidad.setGeneracionElectricidad(1800);
        electricidad.setConsumoElectricidad(1400);
        when(electricidadRepository.save(any(ElectricidadModel.class))).thenReturn(electricidad);

        MedicionModel medicion = new MedicionModel();
        medicion.setIdMedicion(13);
        medicion.setViento(12);
        medicion.setTemperatura(24);
        medicion.setLumens(5000);
        when(medicionRepository.save(any(MedicionModel.class))).thenReturn(medicion);

        medicionTotalModel medicionTotal = new medicionTotalModel();
        medicionTotal.setIdMedicionTotal(14);
        medicionTotal.setIdBateria(11);
        medicionTotal.setIdElectricidad(12);
        medicionTotal.setIdMedicion(13);
        when(medicionTotalRepository.save(any(medicionTotalModel.class))).thenReturn(medicionTotal);

        totalModel total = new totalModel();
        total.setIdTotal(15);
        total.setIdMedicionTotal(14);
        total.setNumAlerta(2);
        total.setFecha(LocalDateTime.now());
        when(totalRepository.save(any(totalModel.class))).thenAnswer(invocation -> {
            totalModel saved = invocation.getArgument(0);
            assertNotNull(saved.getFecha(), "fecha debe generarse antes del insert en Total");
            return saved;
        });

        RegistroCompletoResponseDTO result = totalService.createCompleteRecord(request);

        assertNotNull(result);
        assertEquals("1.0.0", result.getSchemaVersion());
        assertEquals("status", result.getRecordType());
        assertEquals("energy_node_01", result.getNodeId());
        assertEquals(Integer.valueOf(3), result.getGroup());
        assertEquals("Sistema energético autónomo", result.getSubsystem());
        assertEquals("active", result.getStatus());
        assertEquals("medium", result.getAlertLevel());
        assertNotNull(result.getData());
        assertEquals(24.0, result.getData().getTemperature());
        assertEquals(12.0, result.getData().getWindSpeed());
        assertEquals(5000, result.getData().getLumens());
        assertEquals(230.0, result.getData().getVoltage());
        assertEquals(8.0, result.getData().getCurrent());
        assertEquals(1800.0, result.getData().getGenerationPower());
        assertEquals(1400.0, result.getData().getConsumptionPower());
        assertEquals(85.0, result.getData().getStoredEnergy());
        assertEquals(76.0, result.getData().getBatteryLevel());
        assertEquals("Central Norte", result.getData().getOperationMode());
        assertNotNull(result.getMetadata());
        assertEquals("degC", result.getMetadata().getUnits().get("temperature"));
        assertEquals("valid", result.getMetadata().getQuality().get("voltage"));
        verify(totalRepository).save(any(totalModel.class));
    }

    @Test
    void shouldExposeLatestJoinedRecordWithoutForeignKeys() {
        RegistroCompletoResponseDTO expected = new RegistroCompletoResponseDTO();
        expected.setFecha(LocalDateTime.of(2025, 1, 15, 10, 30, 0));
        expected.setAlertLevel("normal");
        expected.setData(new RegistroCompletoDataDTO(
                22.0, 11.0, 4800, 220.0, 7.0, 1700.0, 1300.0, 90.0, 80.0, "Central Norte"
        ));
        RegistroCompletoMetadataDTO metadata = new RegistroCompletoMetadataDTO();
        metadata.setEnvironment("test");
        metadata.setSourceType("simulated");
        metadata.setAlertEvaluated(true);
        metadata.setClockSynced(true);
        metadata.getUnits().put("temperature", "degC");
        metadata.getUnits().put("voltage", "V");
        metadata.getQuality().put("temperature", "valid");
        metadata.getQuality().put("voltage", "valid");
        expected.setMetadata(metadata);

        when(totalRepository.findUltimosRegistrosCompleto()).thenReturn(java.util.List.of(expected));

        RegistroCompletoResponseDTO result = totalService.getUltimoRegistroCompleto();

        assertNotNull(result);
        assertEquals(LocalDateTime.of(2025, 1, 15, 10, 30, 0), result.getFecha());
        assertEquals("normal", result.getAlertLevel());
        assertNotNull(result.getData());
        assertEquals("Central Norte", result.getData().getOperationMode());
        assertEquals(90.0, result.getData().getStoredEnergy());
        assertEquals(80.0, result.getData().getBatteryLevel());
        assertEquals(220.0, result.getData().getVoltage());
        assertEquals("V", result.getMetadata().getUnits().get("voltage"));
        assertEquals("valid", result.getMetadata().getQuality().get("temperature"));
    }
}
