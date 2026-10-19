package com.octobyte.util;

public class ParsedData {
    private Integer lumens;
    private Integer windSpeed;
    private Integer temperature;
    private Integer voltage;

    public Integer getLumens() {
        return lumens;
    }

    public void setLumens(Integer lumens) {
        this.lumens = lumens;
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

    public Integer getVoltage() {
        return voltage;
    }

    public void setVoltage(Integer voltage) {
        this.voltage = voltage;
    }
}
