package com.example.api.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.UUID;

public class RegistroCompletoResponseDTO {
    @JsonProperty("schema_version")
    private String schemaVersion = "1.0.0";

    @JsonProperty("record_id")
    private String recordId = UUID.randomUUID().toString();

    @JsonProperty("record_type")
    private String recordType = "status";

    @JsonProperty("node_id")
    private String nodeId = "energy_node_01";

    @JsonProperty("group")
    private Integer group = 3;

    @JsonProperty("subsystem")
    private String subsystem = "Sistema energético autónomo";

    @JsonProperty("timestamp")
    private LocalDateTime fecha;

    @JsonProperty("status")
    private String status = "active";

    @JsonProperty("alert_level")
    private String alertLevel;

    @JsonProperty("data")
    private RegistroCompletoDataDTO data;

    @JsonProperty("metadata")
    private RegistroCompletoMetadataDTO metadata;

    public RegistroCompletoResponseDTO() {
    }

    public RegistroCompletoResponseDTO(LocalDateTime fecha, Integer numAlerta, String nombreOperativo,
                                      Integer energiaGuardada, Integer nivelPorcentaje,
                                      Integer voltaje, Integer corriente, Integer generacionElectricidad,
                                      Integer consumoElectricidad, Integer viento, Integer temperatura,
                                      Integer lumens) {
        this.fecha = fecha;
        this.alertLevel = mapAlertLevel(numAlerta);
        this.data = new RegistroCompletoDataDTO(
                temperatura != null ? temperatura.doubleValue() : 0.0,
                viento != null ? viento.doubleValue() : 0.0,
                lumens,
                voltaje != null ? voltaje.doubleValue() : 0.0,
                corriente != null ? corriente.doubleValue() : 0.0,
                generacionElectricidad != null ? generacionElectricidad.doubleValue() : 0.0,
                consumoElectricidad != null ? consumoElectricidad.doubleValue() : 0.0,
                energiaGuardada != null ? energiaGuardada.doubleValue() : 0.0,
                nivelPorcentaje != null ? nivelPorcentaje.doubleValue() : 0.0,
                nombreOperativo
        );
        this.metadata = buildMetadata();
    }

    private RegistroCompletoMetadataDTO buildMetadata() {
        RegistroCompletoMetadataDTO dto = new RegistroCompletoMetadataDTO();
        dto.setEnvironment("test");
        dto.setSourceType("simulated");
        dto.setAlertEvaluated(true);
        dto.setClockSynced(true);
        dto.getUnits().put("temperature", "degC");
        dto.getUnits().put("lumens", "lm");
        dto.getUnits().put("wind_speed", "m/s");
        dto.getUnits().put("voltage", "V");
        dto.getUnits().put("current", "A");
        dto.getUnits().put("generation_power", "W");
        dto.getUnits().put("consumption_power", "W");
        dto.getUnits().put("stored_energy", "Wh");
        dto.getUnits().put("battery_level", "%");
        dto.getUnits().put("operation_mode", "string");

        dto.getQuality().put("temperature", "valid");
        dto.getQuality().put("lumens", "valid");
        dto.getQuality().put("wind_speed", "valid");
        dto.getQuality().put("voltage", "valid");
        dto.getQuality().put("current", "valid");
        dto.getQuality().put("generation_power", "valid");
        dto.getQuality().put("consumption_power", "valid");
        dto.getQuality().put("stored_energy", "valid");
        dto.getQuality().put("battery_level", "valid");
        dto.getQuality().put("operation_mode", "valid");
        return dto;
    }

    public static String mapAlertLevel(Integer numAlerta) {
        if (numAlerta == null) return "normal";
        if (numAlerta <= 0) return "normal";
        if (numAlerta == 1) return "low";
        if (numAlerta == 2) return "medium";
        if (numAlerta == 3) return "high";
        return "critical";
    }

    public String getSchemaVersion() { return schemaVersion; }
    public void setSchemaVersion(String schemaVersion) { this.schemaVersion = schemaVersion; }

    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }

    public String getRecordType() { return recordType; }
    public void setRecordType(String recordType) { this.recordType = recordType; }

    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }

    public Integer getGroup() { return group; }
    public void setGroup(Integer group) { this.group = group; }

    public String getSubsystem() { return subsystem; }
    public void setSubsystem(String subsystem) { this.subsystem = subsystem; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAlertLevel() { return alertLevel; }
    public void setAlertLevel(String alertLevel) { this.alertLevel = alertLevel; }

    public RegistroCompletoDataDTO getData() { return data; }
    public void setData(RegistroCompletoDataDTO data) { this.data = data; }

    public RegistroCompletoMetadataDTO getMetadata() { return metadata; }
    public void setMetadata(RegistroCompletoMetadataDTO metadata) { this.metadata = metadata; }
}
