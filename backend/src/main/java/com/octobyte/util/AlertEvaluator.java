package com.octobyte.util;

public class AlertEvaluator {

    public static int evaluate(Integer voltage, Integer temperature, Integer batteryLevel) {
        int alert = 0;

        if (voltage != null && voltage < 1) {
            alert = Math.max(alert, 2);
        }
        if (temperature != null && temperature > 50) {
            alert = Math.max(alert, 3);
        }
        if (batteryLevel != null && batteryLevel < 20) {
            alert = Math.max(alert, 2);
        }
        if (batteryLevel != null && batteryLevel < 10) {
            alert = Math.max(alert, 3);
        }

        return alert;
    }
}
