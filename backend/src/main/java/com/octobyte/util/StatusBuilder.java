package com.octobyte.util;

import com.octobyte.model.Total;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public class StatusBuilder {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static Map<String, Object> build(Total total) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("temperature", total.getMedicionTotal().getMedicion().getTemperatura());
        data.put("wind_speed", total.getMedicionTotal().getMedicion().getViento());
        data.put("lumens", total.getMedicionTotal().getMedicion().getLumens());
        data.put("voltage", total.getMedicionTotal().getElectricidad().getVoltaje());
        data.put("current", total.getMedicionTotal().getElectricidad().getCorriente());
        data.put("generation_power", total.getMedicionTotal().getElectricidad().getGeneracionElectricidad());
        data.put("consumption_power", total.getMedicionTotal().getElectricidad().getConsumoElectricidad());
        data.put("stored_energy", total.getMedicionTotal().getBateria().getEnergiaGuardada());
        data.put("battery_level", total.getMedicionTotal().getBateria().getNivelPorcentaje());
        data.put("operation_mode", total.getMedicionTotal().getBateria().getOperativo().getNombre());

        Map<String, Object> units = new LinkedHashMap<>();
        units.put("temperature", "degC");
        units.put("wind_speed", "m/s");
        units.put("lumens", "lm");
        units.put("voltage", "V");
        units.put("current", "A");
        units.put("generation_power", "W");
        units.put("consumption_power", "W");
        units.put("stored_energy", "Wh");
        units.put("battery_level", "%");
        units.put("operation_mode", "string");

        Map<String, Object> quality = new LinkedHashMap<>();
        for (String key : data.keySet()) {
            quality.put(key, data.get(key) != null ? "valid" : "invalid");
        }

        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("environment", "test");
        metadata.put("source_type", "simulated");
        metadata.put("alert_evaluated", true);
        metadata.put("clock_synced", true);
        metadata.put("units", units);
        metadata.put("quality", quality);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("schema_version", "1.0.0");
        response.put("record_id", String.valueOf(total.getIdTotal()));
        response.put("record_type", "status");
        response.put("node_id", "energy_node_01");
        response.put("group", 3);
        response.put("subsystem", "Sistema energético autónomo");
        response.put("timestamp", total.getFecha().format(FORMATTER));
        response.put("status", "active");
        response.put("alert_level", mapAlertLevel(total.getNumAlerta()));
        response.put("data", data);
        response.put("metadata", metadata);

        return response;
    }

    private static String mapAlertLevel(Integer numAlerta) {
        if (numAlerta == null || numAlerta <= 0) return "normal";
        if (numAlerta == 1) return "low";
        if (numAlerta == 2) return "medium";
        if (numAlerta == 3) return "high";
        return "critical";
    }
}
