package com.example.api.Repositories;

import com.example.api.DTO.RegistroCompletoResponseDTO;
import com.example.api.Models.totalModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface totalRepository extends JpaRepository<totalModel, Integer> {
    Optional<totalModel> findTopByOrderByIdTotalDesc();

    @Query(value = """
        SELECT new com.example.api.DTO.RegistroCompletoResponseDTO(
            t.fecha,
            t.numAlerta,
            o.nombre,
            b.energiaGuardada,
            b.nivelPorcentaje,
            e.voltaje,
            e.corriente,
            e.generacionElectricidad,
            e.consumoElectricidad,
            m.viento,
            m.temperatura,
            m.lumens
        )
        FROM totalModel t
        JOIN medicionTotalModel mt ON mt.idMedicionTotal = t.idMedicionTotal
        JOIN BateriaModel b ON b.idBateria = mt.idBateria
        JOIN OperativoModel o ON o.idOperativo = b.idOperativo
        JOIN ElectricidadModel e ON e.idElectricidad = mt.idElectricidad
        JOIN MedicionModel m ON m.idMedicion = mt.idMedicion
        ORDER BY t.idTotal DESC
        """)
    List<RegistroCompletoResponseDTO> findUltimosRegistrosCompleto();
}
