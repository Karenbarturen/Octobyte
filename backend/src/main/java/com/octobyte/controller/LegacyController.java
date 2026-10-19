package com.octobyte.controller;

import com.octobyte.model.Total;
import com.octobyte.repository.TotalRepository;
import com.octobyte.util.StatusBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
public class LegacyController {

    private final TotalRepository totalRepository;

    public LegacyController(TotalRepository totalRepository) {
        this.totalRepository = totalRepository;
    }

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("message", "Backend Octobyte activo");
        info.put("version", "1.0.0");
        info.put("endpoints", Map.of(
                "GET /api/v1/status", "Último estado",
                "GET /api/v1/records", "Historial paginado",
                "POST /api/v1/records", "Insertar registro manual",
                "GET /api/v1/operation-modes", "Listar modos operativos"
        ));
        return ResponseEntity.ok(info);
    }

    @GetMapping("/signal")
    public ResponseEntity<?> lastSignal() {
        Optional<Total> last = totalRepository.findTopByOrderByIdTotalDesc();
        if (last.isEmpty()) {
            Map<String, Object> empty = new HashMap<>();
            empty.put("lastSignal", null);
            empty.put("totalReceived", 0);
            return ResponseEntity.ok(empty);
        }
        return ResponseEntity.ok(StatusBuilder.build(last.get()));
    }

    @GetMapping("/signals")
    public ResponseEntity<?> history() {
        List<Total> records = totalRepository.findAll(PageRequest.of(0, 100)).getContent();
        List<Map<String, Object>> response = records.stream()
                .map(StatusBuilder::build)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
