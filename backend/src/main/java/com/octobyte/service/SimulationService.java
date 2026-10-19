package com.octobyte.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@EnableScheduling
public class SimulationService {

    private final RecordService recordService;

    @Value("${esp32.simulation.timeout-seconds:60}")
    private int timeoutSeconds;

    @Value("${esp32.simulation.enabled:true}")
    private boolean enabled;

    public SimulationService(RecordService recordService) {
        this.recordService = recordService;
    }

    @Scheduled(fixedDelay = 1000)
    public void simulateIfInactive() {
        if (!enabled) return;

        LocalDateTime last = recordService.getLastRecordTime();
        long secondsSinceLast = ChronoUnit.SECONDS.between(last, LocalDateTime.now());

        if (secondsSinceLast >= timeoutSeconds) {
            recordService.createSimulated();
        }
    }
}
