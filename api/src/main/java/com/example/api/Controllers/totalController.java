package com.example.api.Controllers;

import com.example.api.DTO.RegistroCompletoRequestDTO;
import com.example.api.DTO.RegistroCompletoResponseDTO;
import com.example.api.DTO.totalRequestDTO;
import com.example.api.DTO.ultimoTotalDTO;
import com.example.api.Models.totalModel;
import com.example.api.Services.totalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
public class totalController {

    private final totalService totalService;

    public totalController(totalService totalService) {
        this.totalService = totalService;
    }

    @GetMapping("/totales")
    public List<totalModel> getAllTotales() {
        return totalService.getAllTotales();
    }

    @GetMapping("/totales/ultimo")
    public ResponseEntity<RegistroCompletoResponseDTO> getUltimoTotal() {
        return ResponseEntity.ok(totalService.getUltimoRegistroCompleto());
    }

    @PostMapping("/totales")
    public ResponseEntity<RegistroCompletoResponseDTO> createTotal(@RequestBody RegistroCompletoRequestDTO request) {
        return ResponseEntity.ok(totalService.createCompleteRecord(request));
    }

    @PostMapping("/registro-completo")
    public ResponseEntity<RegistroCompletoResponseDTO> createCompleteRecord(@RequestBody RegistroCompletoRequestDTO request) {
        return ResponseEntity.ok(totalService.createCompleteRecord(request));
    }

    @GetMapping("/registro-completo/ultimo")
    public ResponseEntity<RegistroCompletoResponseDTO> getUltimoRegistroCompleto() {
        return ResponseEntity.ok(totalService.getUltimoRegistroCompleto());
    }
}
