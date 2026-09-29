package com.proyecto.veterinaria.controller;

import com.proyecto.veterinaria.dto.MascotaDTO;
import com.proyecto.veterinaria.dto.MessageResponse;
import com.proyecto.veterinaria.service.MascotaService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mascotas")
@CrossOrigin (origins = "http://localhost:5173/")
public class MascotaController {

    private final MascotaService mascotaService;

    public MascotaController(MascotaService mascotaService) {
        this.mascotaService = mascotaService;
    }

    @GetMapping("/activas")
    public List<MascotaDTO> mostrarActivas() {
        return mascotaService.filtroActivas();
    }

    @GetMapping("/dueno/{idDueno}")
    public List<MascotaDTO> porDueno(@PathVariable Integer idDueno) {
        return mascotaService.filtroIdDueno(idDueno);
    }

    @GetMapping("/especie/{especie}")
    public List<MascotaDTO> porEspecie(@PathVariable String especie) {
        return mascotaService.filtroEspecie(especie);
    }

    @GetMapping("/buscar")
    public List<MascotaDTO> buscarPorNombre(@RequestParam String nombre) {
        return mascotaService.filtroNombre(nombre);
    }

    @GetMapping
    public List<MascotaDTO> mostrarTodas() {
        return mascotaService.findAll();
    }

    @GetMapping("/{id}")
    public MascotaDTO mostrarUna(@PathVariable Integer id) {
        return mascotaService.findById(id);
    }

    @PostMapping
    public ResponseEntity<MessageResponse> agregar(@Vañid @RequestBody MascotaDTO dto) {
        try {
            mascotaService.agregar(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new MessageResponse("Mascota creada con exito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error al crear la mascota"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> modificar(@valid @PathVariable Integer id, @RequestBody MascotaDTO dto) {
        try {
            mascotaService.modificar(id, dto);
            return ResponseEntity.ok(new MessageResponse("Mascota actualizada con exito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error al actualizar la mascota"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> eliminar(@PathVariable Integer id) {
        try {
            mascotaService.eliminar(id);
            return ResponseEntity.ok(new MessageResponse("Mascota eliminada con exito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error al eliminar la mascota"));
        }
    }

    @PatchMapping("/{id}/anular")
    public ResponseEntity<MessageResponse> anular(@PathVariable Integer id) {
        try {
            mascotaService.anular(id);
            return ResponseEntity.ok(new MessageResponse("Mascota anulada con exito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error al anular la mascota"));
        }
    }
}
