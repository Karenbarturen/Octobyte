package com.octobyte.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Esp32Parser {

    private static final Logger log = LoggerFactory.getLogger(Esp32Parser.class);

    public static final String START_MARKER = "===== MEDICIONES =====";
    public static final String END_MARKER = "======================";

    public static ParsedData parse(String block) {
        ParsedData data = new ParsedData();

        String[] lines = block.split("\\r?\\n");
        for (String rawLine : lines) {
            String line = rawLine.trim();
            if (line.isEmpty() || line.equals(START_MARKER) || line.equals(END_MARKER)) {
                continue;
            }

            if (line.startsWith("Luz (ADC):")) {
                data.setLumens(extractInt(line));
            } else if (line.startsWith("Velocidad tangencial estimada:")) {
                data.setWindSpeed(extractFloatAsInt(line));
            } else if (line.startsWith("Temperatura:")) {
                if (line.contains("Error")) {
                    data.setTemperature(null);
                } else {
                    data.setTemperature(extractFloatAsInt(line));
                }
            } else if (line.startsWith("Voltaje estimado del panel:")) {
                data.setVoltage(extractFloatAsInt(line));
            }
        }

        return data;
    }

    private static Integer extractInt(String line) {
        try {
            String number = line.replaceAll("[^0-9-]", "");
            return Integer.parseInt(number);
        } catch (NumberFormatException e) {
            log.warn("No se pudo extraer entero de: {}", line);
            return null;
        }
    }

    private static Integer extractFloatAsInt(String line) {
        try {
            String[] parts = line.split(":");
            if (parts.length < 2) return null;
            String valuePart = parts[1].replaceAll("[^0-9.\\-]", "");
            float value = Float.parseFloat(valuePart);
            return Math.round(value);
        } catch (NumberFormatException e) {
            log.warn("No se pudo extraer float de: {}", line);
            return null;
        }
    }

    public static boolean isCompleteBlock(StringBuilder buffer) {
        String text = buffer.toString();
        return text.contains(START_MARKER) && text.contains(END_MARKER);
    }

    public static String extractBlock(StringBuilder buffer) {
        String text = buffer.toString();
        int start = text.indexOf(START_MARKER);
        int end = text.indexOf(END_MARKER, start);
        if (start == -1 || end == -1) return null;

        String block = text.substring(start, end + END_MARKER.length());
        buffer.delete(0, end + END_MARKER.length());
        return block;
    }
}
