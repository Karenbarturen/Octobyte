package com.octobyte.controller;

import com.octobyte.dto.RecordRequestDto;
import com.octobyte.model.Total;
import com.octobyte.repository.TotalRepository;
import com.octobyte.service.RecordService;
import com.octobyte.util.StatusBuilder;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class RecordController {

    private final RecordService recordService;
    private final TotalRepository totalRepository;

    public RecordController(RecordService recordService, TotalRepository totalRepository) {
        this.recordService = recordService;
        this.totalRepository = totalRepository;
    }

    @PostMapping("/records")
    public ResponseEntity<?> createRecord(@Valid @RequestBody RecordRequestDto dto) {
        try {
            Total total = recordService.createFromDto(dto);
            return ResponseEntity.ok(StatusBuilder.build(total));
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> getStatus() {
        Optional<Total> last = totalRepository.findTopByOrderByIdTotalDesc();
        if (last.isEmpty()) {
            Map<String, Object> empty = new HashMap<>();
            empty.put("status", "no_data");
            empty.put("message", "No hay registros disponibles");
            return ResponseEntity.ok(empty);
        }
        return ResponseEntity.ok(StatusBuilder.build(last.get()));
    }

    @GetMapping("/records")
    public ResponseEntity<?> getRecords(
            @RequestParam(defaultValue = "0") Integer cursor,
            @RequestParam(defaultValue = "10") Integer limit) {

        Pageable pageable = PageRequest.of(0, Math.min(limit, 100));
        List<Total> records;
        if (cursor == 0) {
            records = totalRepository.findAll(pageable).getContent();
        } else {
            records = totalRepository.findByIdTotalLessThanOrderByIdTotalDesc(cursor, pageable);
        }

        List<Map<String, Object>> response = records.stream()
                .map(StatusBuilder::build)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
