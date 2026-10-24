package com.proyecto.veterinaria.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.proyecto.veterinaria.dto.CitaDTO;
import com.proyecto.veterinaria.dto.HojaCitaDTO;
import com.proyecto.veterinaria.entity.Cita;
import com.proyecto.veterinaria.entity.DetalleCita;
import com.proyecto.veterinaria.entity.Medicamentos;
import com.proyecto.veterinaria.entity.Medico;
import com.proyecto.veterinaria.exception.RecursoNoEncontradoException;
import com.proyecto.veterinaria.exception.ReglaNegocioException;
import com.proyecto.veterinaria.repository.CitaRepository;
import com.proyecto.veterinaria.repository.ConsultaRepository;
import com.proyecto.veterinaria.repository.DetalleCitaRepository;
import com.proyecto.veterinaria.repository.MedicoRepository;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CitaServiceTest {

    @Mock
    private CitaRepository citaRepository;
    @Mock
    private ConsultaRepository consultaRepository;
    @Mock
    private DetalleCitaService detalleCitaService;
    @Mock
    private DetalleCitaRepository detalleCitaRepository;
    @Mock
    private MedicoRepository medicoRepository;

        @Mock
    private HistorialService historialService;

    @InjectMocks
    private CitaService citaService;

    private static Date enUnaHora() {
        return new Date(System.currentTimeMillis() + 60L * 60 * 1000);
    }

    private static CitaDTO citaDto(Date fecha) {
        CitaDTO dto = new CitaDTO();
        dto.setFecha(fecha);
        dto.setMotivo("Vacuna");
        dto.setEstado(true);
        dto.setIdMascota(1);
        dto.setIdMedico(2);
        dto.setIdUsuario(3);
        return dto;
    }

    private static Cita citaConId(Integer id) {
        Cita cita = new Cita(id);
        cita.setEstado(true);
        return cita;
    }

    @Test
    void crear_guardaLaCitaConTotalCeroYBloqueaAlMedico() {
        when(medicoRepository.bloquearPorId(2)).thenReturn(Optional.of(new Medico(2)));
        when(citaRepository.findByIdMedico_IdMedicoAndEstadoTrueAndFechaBetween(eq(2), any(Date.class),
                any(Date.class))).thenReturn(List.of());
        when(citaRepository.save(any(Cita.class))).thenAnswer(inv -> {
            Cita c = inv.getArgument(0);
            c.setIdCita(10);
            return c;
        });

        CitaDTO resultado = citaService.crear(citaDto(enUnaHora()));

        assertThat(resultado.getIdCita()).isEqualTo(10);
        assertThat(resultado.getTotal()).isEqualByComparingTo(BigDecimal.ZERO);
        verify(medicoRepository).bloquearPorId(2);
    }

    @Test
    void crear_conFechaPasada_lanzaReglaNegocio() {
        when(medicoRepository.bloquearPorId(2)).thenReturn(Optional.of(new Medico(2)));
        Date ayer = new Date(System.currentTimeMillis() - 24L * 60 * 60 * 1000);

        assertThatThrownBy(() -> citaService.crear(citaDto(ayer)))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("fecha pasada");
        verify(citaRepository, never()).save(any(Cita.class));
    }

    @Test
    void crear_conChoqueDeHorario_lanzaReglaNegocio() {
        when(medicoRepository.bloquearPorId(2)).thenReturn(Optional.of(new Medico(2)));
        when(citaRepository.findByIdMedico_IdMedicoAndEstadoTrueAndFechaBetween(eq(2), any(Date.class),
                any(Date.class))).thenReturn(List.of(citaConId(5)));

        assertThatThrownBy(() -> citaService.crear(citaDto(enUnaHora())))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("ya tiene una cita");
        verify(citaRepository, never()).save(any(Cita.class));
    }

    @Test
    void crear_conMedicoInexistente_lanzaNoEncontrado() {
        assertThatThrownBy(() -> citaService.crear(citaDto(enUnaHora())))
                .isInstanceOf(RecursoNoEncontradoException.class);
        verify(citaRepository, never()).save(any(Cita.class));
    }

    @Test
    void modificar_ignoraLaPropiaCitaAlValidarElChoque() {
        Cita existente = citaConId(7);
        when(medicoRepository.bloquearPorId(2)).thenReturn(Optional.of(new Medico(2)));
        when(citaRepository.findById(7)).thenReturn(Optional.of(existente));
        when(citaRepository.findByIdMedico_IdMedicoAndEstadoTrueAndFechaBetween(eq(2), any(Date.class),
                any(Date.class))).thenReturn(List.of(existente));
        when(citaRepository.save(any(Cita.class))).thenAnswer(inv -> inv.getArgument(0));

        assertThatCode(() -> citaService.modificar(7, citaDto(enUnaHora()))).doesNotThrowAnyException();
        verify(citaRepository).save(existente);
    }

    @Test
    void anular_anulaLosDetallesPrimeroYLuegoLaCita() {
        Cita cita = citaConId(4);
        when(citaRepository.existsById(4)).thenReturn(true);
        when(citaRepository.findById(4)).thenReturn(Optional.of(cita));
        when(citaRepository.save(any(Cita.class))).thenAnswer(inv -> inv.getArgument(0));

        citaService.anular(4);

        InOrder orden = inOrder(detalleCitaService, citaRepository);
        orden.verify(detalleCitaService).anularPorCita(4);
        orden.verify(citaRepository).findById(4);
        assertThat(cita.getEstado()).isFalse();
    }

    @Test
    void anular_citaInexistente_lanzaNoEncontrado() {
        when(citaRepository.existsById(99)).thenReturn(false);

        assertThatThrownBy(() -> citaService.anular(99))
                .isInstanceOf(RecursoNoEncontradoException.class);
        verify(detalleCitaService, never()).anularPorCita(any());
    }

    @Test
    void eliminar_conConsultas_lanzaReglaNegocio() {
        when(consultaRepository.existsByIdCita_IdCita(9)).thenReturn(true);

        assertThatThrownBy(() -> citaService.eliminar(9))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("consultas");
        verify(citaRepository, never()).deleteById(any());
    }

    @Test
    void eliminar_sinConsultas_devuelveElStockYBorra() {
        when(consultaRepository.existsByIdCita_IdCita(9)).thenReturn(false);

        citaService.eliminar(9);

        verify(detalleCitaService).eliminarPorCita(9);
        verify(citaRepository).deleteById(9);
    }

    @Test
    void findById_inexistente_lanzaNoEncontrado() {
        when(citaRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> citaService.findById(1))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void hoja_conDetalleSinPrecio_noFalla() {
        Medicamentos medicamento = new Medicamentos(2);
        medicamento.setNombre("Amoxicilina");
        DetalleCita detalle = new DetalleCita(1);
        detalle.setEstado(true);
        detalle.setCantidad(2);
        detalle.setIdMedicamento(medicamento);
        detalle.setPrecioAplicado(null);

        when(citaRepository.findById(3)).thenReturn(Optional.of(citaConId(3)));
        when(consultaRepository.findByIdCita_IdCita(3)).thenReturn(List.of());
        when(detalleCitaRepository.findByIdCita_IdCita(3)).thenReturn(List.of(detalle));

        HojaCitaDTO hoja = citaService.hoja(3);

        assertThat(hoja.getLineas()).hasSize(1);
        assertThat(hoja.getTotal()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
