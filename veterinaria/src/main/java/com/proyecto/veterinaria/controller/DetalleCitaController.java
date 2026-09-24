package com.proyecto.veterinaria.controller;

import com.proyecto.veterinaria.dto.DetalleCitaDTO;
import com.proyecto.veterinaria.service.DetalleCitaService;
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
    public DetalleCitaDTO agregar(@RequestBody DetalleCitaDTO dto) {
        return detalleCitaService.crear(dto);
    }

    @PutMapping("/{id}")
    public DetalleCitaDTO modificar(@PathVariable Integer id, @RequestBody DetalleCitaDTO dto) {
        return detalleCitaService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
        detalleCitaService.eliminar(id);
    }

    @PatchMapping("/{id}/anular")
    public DetalleCitaDTO anular(@PathVariable Integer id) {
        return detalleCitaService.anular(id);
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
