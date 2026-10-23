package com.proyecto.veterinaria.controller;

import com.proyecto.veterinaria.dto.DetalleCitaDTO;
import com.proyecto.veterinaria.dto.MessageResponse;
import com.proyecto.veterinaria.service.DetalleCitaService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/detalle-citas")
public class DetalleCitaController {

    private final DetalleCitaService detalleCitaService;

    public DetalleCitaController(DetalleCitaService detalleCitaService) {
        this.detalleCitaService = detalleCitaService;
    }

    @GetMapping
    public List<DetalleCitaDTO> mostrarTodos() {
        return detalleCitaService.findAll();
    }

    @GetMapping("/{id}")
    public DetalleCitaDTO mostrarUno(@PathVariable Integer id) {
        return detalleCitaService.findById(id);
    }

    @PostMapping
    public ResponseEntity<MessageResponse> agregar(@Valid @RequestBody DetalleCitaDTO dto) {
        detalleCitaService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponse("Detalle de cita creado con exito"));

    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> modificar(@PathVariable Integer id, @Valid @RequestBody DetalleCitaDTO dto) {
        detalleCitaService.actualizar(id, dto);
        return ResponseEntity.ok(new MessageResponse("Detalle de cita actualizado con exito"));
    }

    @PatchMapping("/{id}/anular")
    public ResponseEntity<MessageResponse> anular(@PathVariable Integer id) {
        detalleCitaService.anular(id);
        return ResponseEntity.ok(new MessageResponse("Detalle de cita anulado con exito"));
    }

    @GetMapping("/activos")
    public List<DetalleCitaDTO> mostrarActivos() {
        return detalleCitaService.findActivos();
    }

    @GetMapping("/cita/{idCita}")
    public List<DetalleCitaDTO> porCita(@PathVariable Integer idCita) {
        return detalleCitaService.findByCita(idCita);
    }

    @GetMapping("/medicamento/{idMedicamento}")
    public List<DetalleCitaDTO> porMedicamento(@PathVariable Integer idMedicamento) {
        return detalleCitaService.findByMedicamento(idMedicamento);
    }

    @GetMapping("/tratamiento/{idTratamiento}")
    public List<DetalleCitaDTO> porTratamiento(@PathVariable Integer idTratamiento) {
        return detalleCitaService.findByTratamiento(idTratamiento);
    }
}
