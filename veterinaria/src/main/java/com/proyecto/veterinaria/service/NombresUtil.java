package com.proyecto.veterinaria.service;

import com.proyecto.veterinaria.entity.Dueno;
import com.proyecto.veterinaria.entity.Mascota;
import com.proyecto.veterinaria.entity.Medico;
import com.proyecto.veterinaria.entity.Usuarios;
import com.proyecto.veterinaria.entity.Cita;
import com.proyecto.veterinaria.entity.Medicamentos;
import com.proyecto.veterinaria.entity.Tratamiento;
import com.proyecto.veterinaria.entity.Perfil;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class NombresUtil {

    private NombresUtil() {
    }

    // Une las partes que no estén vacías: ("Juan", null, "Pérez") -> "Juan Pérez"
    private static String unir(String... partes) {
        String texto = Arrays.stream(partes)
                .filter(p -> p != null && !p.isBlank())
                .collect(Collectors.joining(" "));
        return texto.isEmpty() ? null : texto;
    }

    public static String dueno(Dueno dueno) {
        return dueno == null ? null : unir(dueno.getNombre(), dueno.getApellido());
    }

    public static String mascota(Mascota mascota) {
        return mascota == null ? null : mascota.getNombre();
    }

    public static String medico(Medico medico) {
        return medico == null ? null : unir(medico.getNombre(), medico.getApellido());
    }

    public static String usuario(Usuarios usuario) {
        return usuario == null ? null : usuario.getNombre();
    }

    public static String perfil(Perfil perfil) {
        return perfil == null ? null : perfil.getNombrePerfil();
    }

    public static String cita(Cita cita) {
        if (cita == null)
            return null;
        String mascota = mascota(cita.getIdMascota());
        String motivo = cita.getMotivo();
        if (mascota == null || motivo == null || motivo.isBlank()) {
            return mascota != null ? mascota : motivo;
        }
        return mascota + " - " + motivo;
    }

    public static String medicamento(Medicamentos medicamento) {
        return medicamento == null ? null : medicamento.getNombre();
    }

    public static String tratamiento(Tratamiento tratamiento) {
        return tratamiento == null ? null : tratamiento.getNombre();
    }
}
