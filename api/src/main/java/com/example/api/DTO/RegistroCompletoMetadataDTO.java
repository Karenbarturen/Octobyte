package com.example.api.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedHashMap;
import java.util.Map;

public class RegistroCompletoMetadataDTO {
    @JsonProperty("environment")
    private String environment;

    @JsonProperty("source_type")
    private String sourceType;

    @JsonProperty("alert_evaluated")
    private Boolean alertEvaluated;

    @JsonProperty("clock_synced")
    private Boolean clockSynced;

    @JsonProperty("units")
    private Map<String, String> units = new LinkedHashMap<>();

    @JsonProperty("quality")
    private Map<String, String> quality = new LinkedHashMap<>();

    public RegistroCompletoMetadataDTO() {
    }

    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }

    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }

    public Boolean getAlertEvaluated() { return alertEvaluated; }
    public void setAlertEvaluated(Boolean alertEvaluated) { this.alertEvaluated = alertEvaluated; }

    public Boolean getClockSynced() { return clockSynced; }
    public void setClockSynced(Boolean clockSynced) { this.clockSynced = clockSynced; }

    public Map<String, String> getUnits() { return units; }
    public void setUnits(Map<String, String> units) { this.units = units; }

    public Map<String, String> getQuality() { return quality; }
    public void setQuality(Map<String, String> quality) { this.quality = quality; }
}
