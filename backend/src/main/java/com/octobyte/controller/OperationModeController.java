package com.octobyte.controller;

import com.octobyte.dto.OperationModeDto;
import com.octobyte.model.Operativo;
import com.octobyte.service.OperationModeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/operation-modes")
@CrossOrigin(origins = "*")
public class OperationModeController {

    private final OperationModeService operationModeService;

    public OperationModeController(OperationModeService operationModeService) {
        this.operationModeService = operationModeService;
    }

    @GetMapping
    public ResponseEntity<List<Operativo>> list() {
        return ResponseEntity.ok(operationModeService.findAll());
    }

    @PostMapping
    public ResponseEntity<Operativo> create(@Valid @RequestBody OperationModeDto dto) {
        return ResponseEntity.ok(operationModeService.create(dto));
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<Map<String, String>> activate(@PathVariable Integer id) {
        operationModeService.activate(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Modo operativo activado");
        return ResponseEntity.ok(response);
    }
}
