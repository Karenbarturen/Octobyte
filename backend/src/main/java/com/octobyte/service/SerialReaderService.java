package com.octobyte.service;

import com.fazecast.jSerialComm.SerialPort;
import com.octobyte.util.Esp32Parser;
import com.octobyte.util.ParsedData;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class SerialReaderService {

    private static final Logger log = LoggerFactory.getLogger(SerialReaderService.class);

    private final RecordService recordService;

    @Value("${esp32.serial.baud-rate:9600}")
    private int baudRate;

    @Value("${esp32.serial.default-port:COM3}")
    private String defaultPort;

    private SerialPort serialPort;
    private ExecutorService executor;
    private final StringBuilder buffer = new StringBuilder();

    public SerialReaderService(RecordService recordService) {
        this.recordService = recordService;
    }

    @PostConstruct
    public void start() {
        executor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "esp32-serial-reader");
            t.setDaemon(true);
            return t;
        });
        executor.submit(this::connectAndRead);
    }

    private void connectAndRead() {
        SerialPort[] ports = SerialPort.getCommPorts();
        log.info("Puertos seriales disponibles: {}", Arrays.stream(ports).map(SerialPort::getSystemPortName).toList());

        serialPort = Arrays.stream(ports)
                .filter(p -> p.getSystemPortName().equalsIgnoreCase(defaultPort))
                .findFirst()
                .orElseGet(() -> {
                    if (ports.length > 0) {
                        log.warn("Puerto {} no encontrado, usando el primero disponible: {}", defaultPort, ports[0].getSystemPortName());
                        return ports[0];
                    }
                    return null;
                });

        if (serialPort == null) {
            log.warn("No se encontró ningún puerto serial. El backend funcionará solo con simulador.");
            return;
        }

        serialPort.setBaudRate(baudRate);
        serialPort.setNumDataBits(8);
        serialPort.setNumStopBits(1);
        serialPort.setParity(SerialPort.NO_PARITY);
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 0, 0);

        if (!serialPort.openPort()) {
            log.error("No se pudo abrir el puerto serial {}", serialPort.getSystemPortName());
            return;
        }

        log.info("Puerto serial abierto: {} @ {} baudios", serialPort.getSystemPortName(), baudRate);

        try (InputStream in = serialPort.getInputStream()) {
            byte[] data = new byte[1024];
            while (!Thread.currentThread().isInterrupted()) {
                int len = in.read(data);
                if (len > 0) {
                    log.debug("Bytes recibidos del puerto serial: {}", len);
                    String text = new String(data, 0, len, StandardCharsets.UTF_8);
                    synchronized (buffer) {
                        buffer.append(text);
                        while (Esp32Parser.isCompleteBlock(buffer)) {
                            String block = Esp32Parser.extractBlock(buffer);
                            if (block != null) {
                                ParsedData parsed = Esp32Parser.parse(block);
                                recordService.createFromEsp32(parsed);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error leyendo puerto serial: {}", e.getMessage(), e);
        } finally {
            closePort();
        }
    }

    @PreDestroy
    public void stop() {
        if (executor != null) {
            executor.shutdownNow();
        }
        closePort();
    }

    private void closePort() {
        if (serialPort != null && serialPort.isOpen()) {
            serialPort.closePort();
            log.info("Puerto serial cerrado");
        }
    }
}
