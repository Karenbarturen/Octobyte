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
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
public class ApiAliasController {

    private final RecordService recordService;
    private final TotalRepository totalRepository;

    public ApiAliasController(RecordService recordService, TotalRepository totalRepository) {
        this.recordService = recordService;
        this.totalRepository = totalRepository;
    }

    @GetMapping({"/registro-completo/ultimo", "/totales/ultimo", "/status"})
    public ResponseEntity<?> getUltimoRegistro() {
        Optional<Total> last = totalRepository.findTopByOrderByIdTotalDesc();
        if (last.isEmpty()) {
            Map<String, Object> empty = new HashMap<>();
            empty.put("status", "no_data");
            empty.put("message", "No hay registros disponibles");
            return ResponseEntity.ok(empty);
        }
        return ResponseEntity.ok(StatusBuilder.build(last.get()));
    }

    @GetMapping({"/totales", "/records"})
    public ResponseEntity<?> getRecords(
            @RequestParam(value = "cursor", required = false) Integer cursor,
            @RequestParam(value = "limit", defaultValue = "25") Integer limit) {

        Pageable pageable = PageRequest.of(0, Math.min(limit, 100));
        List<Total> records;
        if (cursor == null || cursor == 0) {
            records = totalRepository.findAll(pageable).getContent();
        } else {
            records = totalRepository.findByIdTotalLessThanOrderByIdTotalDesc(cursor, pageable);
        }

        List<Map<String, Object>> response = records.stream()
                .map(StatusBuilder::build)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PostMapping({"/totales", "/records", "/registro-completo"})
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
}
