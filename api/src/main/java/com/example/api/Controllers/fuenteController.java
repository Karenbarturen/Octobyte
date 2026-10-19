package com.example.api.Controllers;

import com.example.api.Models.fuenteModel;
import com.example.api.Services.fuenteService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {org.springframework.web.bind.annotation.RequestMethod.GET, org.springframework.web.bind.annotation.RequestMethod.POST, org.springframework.web.bind.annotation.RequestMethod.OPTIONS})
public class fuenteController {

    private final fuenteService fuenteService;

    public fuenteController(fuenteService fuenteService) {
        this.fuenteService = fuenteService;
    }

    @GetMapping("/fuentes")
    public List<fuenteModel> getAllFuentes() {
        return fuenteService.getAllFuentes();
    }
}
