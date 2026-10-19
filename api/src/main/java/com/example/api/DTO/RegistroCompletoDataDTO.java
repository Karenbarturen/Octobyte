package com.example.api.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RegistroCompletoDataDTO {
    @JsonProperty("temperature")
    private Double temperature;

    @JsonProperty("wind_speed")
    private Double windSpeed;

    @JsonProperty("lumens")
    private Integer lumens;

    @JsonProperty("voltage")
    private Double voltage;

    @JsonProperty("current")
    private Double current;

    @JsonProperty("generation_power")
    private Double generationPower;

    @JsonProperty("consumption_power")
    private Double consumptionPower;

    @JsonProperty("stored_energy")
    private Double storedEnergy;

    @JsonProperty("battery_level")
    private Double batteryLevel;

    @JsonProperty("operation_mode")
    private String operationMode;

    public RegistroCompletoDataDTO() {
    }

    public RegistroCompletoDataDTO(Double temperature, Double windSpeed, Integer lumens,
                                  Double voltage, Double current, Double generationPower,
                                  Double consumptionPower, Double storedEnergy, Double batteryLevel,
                                  String operationMode) {
        this.temperature = temperature;
        this.windSpeed = windSpeed;
        this.lumens = lumens;
        this.voltage = voltage;
        this.current = current;
        this.generationPower = generationPower;
        this.consumptionPower = consumptionPower;
        this.storedEnergy = storedEnergy;
        this.batteryLevel = batteryLevel;
        this.operationMode = operationMode;
    }

    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }

    public Double getWindSpeed() { return windSpeed; }
    public void setWindSpeed(Double windSpeed) { this.windSpeed = windSpeed; }

    public Integer getLumens() { return lumens; }
    public void setLumens(Integer lumens) { this.lumens = lumens; }

    public Double getVoltage() { return voltage; }
    public void setVoltage(Double voltage) { this.voltage = voltage; }

    public Double getCurrent() { return current; }
    public void setCurrent(Double current) { this.current = current; }

    public Double getGenerationPower() { return generationPower; }
    public void setGenerationPower(Double generationPower) { this.generationPower = generationPower; }

    public Double getConsumptionPower() { return consumptionPower; }
    public void setConsumptionPower(Double consumptionPower) { this.consumptionPower = consumptionPower; }

    public Double getStoredEnergy() { return storedEnergy; }
    public void setStoredEnergy(Double storedEnergy) { this.storedEnergy = storedEnergy; }

    public Double getBatteryLevel() { return batteryLevel; }
    public void setBatteryLevel(Double batteryLevel) { this.batteryLevel = batteryLevel; }

    public String getOperationMode() { return operationMode; }
    public void setOperationMode(String operationMode) { this.operationMode = operationMode; }
}
