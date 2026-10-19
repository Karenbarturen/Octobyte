package com.example.api.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RegistroCompletoRequestDTO {
    @JsonProperty("operation_mode")
    private String operationMode;

    @JsonProperty("stored_energy")
    private Integer storedEnergy;

    @JsonProperty("battery_level")
    private Integer batteryLevel;

    @JsonProperty("voltage")
    private Integer voltage;

    @JsonProperty("current")
    private Integer current;

    @JsonProperty("generation_power")
    private Integer generationPower;

    @JsonProperty("consumption_power")
    private Integer consumptionPower;

    @JsonProperty("wind_speed")
    private Integer windSpeed;

    @JsonProperty("temperature")
    private Integer temperature;

    @JsonProperty("lumens")
    private Integer lumens;

    @JsonProperty("alert_level")
    private String alertLevel;

    public RegistroCompletoRequestDTO() {
    }

    public String getOperationMode() { return operationMode; }
    public void setOperationMode(String operationMode) { this.operationMode = operationMode; }

    public Integer getStoredEnergy() { return storedEnergy; }
    public void setStoredEnergy(Integer storedEnergy) { this.storedEnergy = storedEnergy; }

    public Integer getBatteryLevel() { return batteryLevel; }
    public void setBatteryLevel(Integer batteryLevel) { this.batteryLevel = batteryLevel; }

    public Integer getVoltage() { return voltage; }
    public void setVoltage(Integer voltage) { this.voltage = voltage; }

    public Integer getCurrent() { return current; }
    public void setCurrent(Integer current) { this.current = current; }

    public Integer getGenerationPower() { return generationPower; }
    public void setGenerationPower(Integer generationPower) { this.generationPower = generationPower; }

    public Integer getConsumptionPower() { return consumptionPower; }
    public void setConsumptionPower(Integer consumptionPower) { this.consumptionPower = consumptionPower; }

    public Integer getWindSpeed() { return windSpeed; }
    public void setWindSpeed(Integer windSpeed) { this.windSpeed = windSpeed; }

    public Integer getTemperature() { return temperature; }
    public void setTemperature(Integer temperature) { this.temperature = temperature; }

    public Integer getLumens() { return lumens; }
    public void setLumens(Integer lumens) { this.lumens = lumens; }

    public String getAlertLevel() { return alertLevel; }
    public void setAlertLevel(String alertLevel) { this.alertLevel = alertLevel; }

    public String getNombre() { return operationMode; }
    public void setNombre(String nombre) { this.operationMode = nombre; }

    public Integer getEnergiaGuardada() { return storedEnergy; }
    public void setEnergiaGuardada(Integer energiaGuardada) { this.storedEnergy = energiaGuardada; }

    public Integer getNivelPorcentaje() { return batteryLevel; }
    public void setNivelPorcentaje(Integer nivelPorcentaje) { this.batteryLevel = nivelPorcentaje; }

    public Integer getVoltaje() { return voltage; }
    public void setVoltaje(Integer voltaje) { this.voltage = voltaje; }

    public Integer getCorriente() { return current; }
    public void setCorriente(Integer corriente) { this.current = corriente; }

    public Integer getGeneracionElectricidad() { return generationPower; }
    public void setGeneracionElectricidad(Integer generacionElectricidad) { this.generationPower = generacionElectricidad; }

    public Integer getConsumoElectricidad() { return consumptionPower; }
    public void setConsumoElectricidad(Integer consumoElectricidad) { this.consumptionPower = consumoElectricidad; }

    public Integer getViento() { return windSpeed; }
    public void setViento(Integer viento) { this.windSpeed = viento; }

    public Integer getTemperatura() { return temperature; }
    public void setTemperatura(Integer temperatura) { this.temperature = temperatura; }

    public Integer getNumAlerta() {
        if (alertLevel == null) return null;
        switch (alertLevel.trim().toLowerCase()) {
            case "normal": return 0;
            case "low": return 1;
            case "medium": return 2;
            case "high": return 3;
            case "critical": return 4;
            default: return Integer.parseInt(alertLevel);
        }
    }

    public void setNumAlerta(Integer numAlerta) {
        if (numAlerta == null) {
            this.alertLevel = null;
            return;
        }
        if (numAlerta <= 0) this.alertLevel = "normal";
        else if (numAlerta == 1) this.alertLevel = "low";
        else if (numAlerta == 2) this.alertLevel = "medium";
        else if (numAlerta == 3) this.alertLevel = "high";
        else this.alertLevel = "critical";
    }
}
