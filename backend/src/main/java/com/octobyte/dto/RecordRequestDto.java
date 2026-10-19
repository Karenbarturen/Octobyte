package com.octobyte.dto;

import jakarta.validation.constraints.NotNull;

public class RecordRequestDto {

    @NotNull
    private String operationMode;

    @NotNull
    private Integer storedEnergy;

    @NotNull
    private Integer batteryLevel;

    @NotNull
    private Integer voltage;

    @NotNull
    private Integer current;

    @NotNull
    private Integer generationPower;

    @NotNull
    private Integer consumptionPower;

    @NotNull
    private Integer windSpeed;

    @NotNull
    private Integer temperature;

    @NotNull
    private Integer lumens;

    @NotNull
    private Integer alertLevel;

    public String getOperationMode() {
        return operationMode;
    }

    public void setOperationMode(String operationMode) {
        this.operationMode = operationMode;
    }

    public Integer getStoredEnergy() {
        return storedEnergy;
    }

    public void setStoredEnergy(Integer storedEnergy) {
        this.storedEnergy = storedEnergy;
    }

    public Integer getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(Integer batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public Integer getVoltage() {
        return voltage;
    }

    public void setVoltage(Integer voltage) {
        this.voltage = voltage;
    }

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }

    public Integer getGenerationPower() {
        return generationPower;
    }

    public void setGenerationPower(Integer generationPower) {
        this.generationPower = generationPower;
    }

    public Integer getConsumptionPower() {
        return consumptionPower;
    }

    public void setConsumptionPower(Integer consumptionPower) {
        this.consumptionPower = consumptionPower;
    }

    public Integer getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(Integer windSpeed) {
        this.windSpeed = windSpeed;
    }

    public Integer getTemperature() {
        return temperature;
    }

    public void setTemperature(Integer temperature) {
        this.temperature = temperature;
    }

    public Integer getLumens() {
        return lumens;
    }

    public void setLumens(Integer lumens) {
        this.lumens = lumens;
    }

    public Integer getAlertLevel() {
        return alertLevel;
    }

    public void setAlertLevel(Integer alertLevel) {
        this.alertLevel = alertLevel;
    }
}
