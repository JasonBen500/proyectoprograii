package com.proyecto.veterinaria.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.proyecto.veterinaria.dto.DetalleCitaDTO;
import com.proyecto.veterinaria.entity.Cita;
import com.proyecto.veterinaria.entity.DetalleCita;
import com.proyecto.veterinaria.entity.Medicamentos;
import com.proyecto.veterinaria.entity.Tratamiento;
import com.proyecto.veterinaria.exception.ReglaNegocioException;
import com.proyecto.veterinaria.repository.CitaRepository;
import com.proyecto.veterinaria.repository.DetalleCitaRepository;
import com.proyecto.veterinaria.repository.MedicamentosRepository;
import com.proyecto.veterinaria.repository.TratamientoRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DetalleCitaServiceTest {

    @Mock
    private DetalleCitaRepository detalleCitaRepository;
    @Mock
    private MedicamentosService medicamentosService;
    @Mock
    private CitaRepository citaRepository;
    @Mock
    private MedicamentosRepository medicamentosRepository;
    @Mock
    private TratamientoRepository tratamientoRepository;
        @Mock
    private HistorialService historialService;

    @InjectMocks
    private DetalleCitaService detalleCitaService;

    private static Cita citaActiva(Integer id) {
        Cita cita = new Cita(id);
        cita.setEstado(true);
        return cita;
    }

    private static Medicamentos medicamento(Integer id, String precio, boolean activo) {
        Medicamentos m = new Medicamentos(id);
        m.setNombre("Amoxicilina");
        m.setPrecio(new BigDecimal(precio));
        m.setEstado(activo);
        return m;
    }

    private static Tratamiento tratamiento(Integer id, String precio) {
        Tratamiento t = new Tratamiento(id);
        t.setNombre("Limpieza");
        t.setPrecio(new BigDecimal(precio));
        t.setEstado(true);
        return t;
    }

    private static DetalleCitaDTO dto(Integer idMedicamento, Integer idTratamiento) {
        DetalleCitaDTO dto = new DetalleCitaDTO();
        dto.setIdCita(1);
        dto.setIdMedicamento(idMedicamento);
        dto.setIdTratamiento(idTratamiento);
        dto.setCantidad(3);
        return dto;
    }

    private static DetalleCita detalleDeMedicamento(Integer id, Integer idMedicamento, String precio, int cantidad,
            boolean activo) {
        DetalleCita d = new DetalleCita(id);
        d.setIdCita(new Cita(1));
        d.setIdMedicamento(new Medicamentos(idMedicamento));
        d.setPrecioAplicado(new BigDecimal(precio));
        d.setCantidad(cantidad);
        d.setEstado(activo);
        return d;
    }

    // ---------- crear ----------

    @Test
    void crear_sinMedicamentoNiTratamiento_lanzaReglaNegocio() {
        assertThatThrownBy(() -> detalleCitaService.crear(dto(null, null)))
                .isInstanceOf(ReglaNegocioException.class);
        verify(detalleCitaRepository, never()).save(any(DetalleCita.class));
    }

    @Test
    void crear_conMedicamentoYTratamiento_lanzaReglaNegocio() {
        assertThatThrownBy(() -> detalleCitaService.crear(dto(2, 4)))
                .isInstanceOf(ReglaNegocioException.class);
    }

    @Test
    void crear_enCitaAnulada_lanzaReglaNegocio() {
        Cita anulada = new Cita(1);
        anulada.setEstado(false);
        when(citaRepository.findById(1)).thenReturn(Optional.of(anulada));

        assertThatThrownBy(() -> detalleCitaService.crear(dto(2, null)))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("anulada");
    }

    @Test
    void crear_conMedicamentoAnulado_lanzaReglaNegocio() {
        when(citaRepository.findById(1)).thenReturn(Optional.of(citaActiva(1)));
        when(medicamentosRepository.findById(2)).thenReturn(Optional.of(medicamento(2, "50", false)));

        assertThatThrownBy(() -> detalleCitaService.crear(dto(2, null)))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("anulado");
    }

    @Test
    void crear_usaElPrecioDelCatalogoEIgnoraElDelCliente() {
        Cita cita = citaActiva(1);
        DetalleCitaDTO entrada = dto(2, null);
        entrada.setPrecioAplicado(new BigDecimal("1"));
        when(citaRepository.findById(1)).thenReturn(Optional.of(cita));
        when(medicamentosRepository.findById(2)).thenReturn(Optional.of(medicamento(2, "50", true)));
        when(detalleCitaRepository.save(any(DetalleCita.class))).thenAnswer(inv -> inv.getArgument(0));
        when(detalleCitaRepository.findByIdCita_IdCita(1)).thenAnswer(inv -> {
            DetalleCita guardado = detalleDeMedicamento(1, 2, "50", 3, true);
            return List.of(guardado);
        });

        detalleCitaService.crear(entrada);

        ArgumentCaptor<DetalleCita> captor = ArgumentCaptor.forClass(DetalleCita.class);
        verify(detalleCitaRepository).save(captor.capture());
        assertThat(captor.getValue().getPrecioAplicado()).isEqualByComparingTo("50");
        assertThat(captor.getValue().getEstado()).isTrue();
        verify(medicamentosService).ajustarStock(2, -3);
        assertThat(cita.getTotal()).isEqualByComparingTo("150");
    }

    // ---------- actualizar ----------

    @Test
    void actualizar_deMedicamentoATratamiento_limpiaElMedicamentoYDevuelveElStock() {
        DetalleCita existente = detalleDeMedicamento(9, 2, "50", 2, true);
        DetalleCitaDTO cambio = dto(null, 4);
        cambio.setCantidad(1);
        cambio.setEstado(true);
        when(detalleCitaRepository.findById(9)).thenReturn(Optional.of(existente));
        when(citaRepository.findById(1)).thenReturn(Optional.of(citaActiva(1)));
        when(tratamientoRepository.findById(4)).thenReturn(Optional.of(tratamiento(4, "80")));
        when(detalleCitaRepository.save(any(DetalleCita.class))).thenAnswer(inv -> inv.getArgument(0));
        when(detalleCitaRepository.findByIdCita_IdCita(1)).thenReturn(List.of());

        detalleCitaService.actualizar(9, cambio);

        ArgumentCaptor<DetalleCita> captor = ArgumentCaptor.forClass(DetalleCita.class);
        verify(detalleCitaRepository).save(captor.capture());
        DetalleCita guardado = captor.getValue();
        assertThat(guardado.getIdMedicamento()).isNull();
        assertThat(guardado.getIdTratamiento().getIdTratamiento()).isEqualTo(4);
        assertThat(guardado.getPrecioAplicado()).isEqualByComparingTo("80");
        verify(medicamentosService).ajustarStock(2, 2);
    }

    @Test
    void actualizar_conElMismoItem_conservaElPrecioYaAplicado() {
        DetalleCita existente = detalleDeMedicamento(9, 2, "40", 2, true);
        DetalleCitaDTO cambio = dto(2, null);
        cambio.setCantidad(5);
        cambio.setEstado(true);
        when(detalleCitaRepository.findById(9)).thenReturn(Optional.of(existente));
        when(citaRepository.findById(1)).thenReturn(Optional.of(citaActiva(1)));
        when(detalleCitaRepository.save(any(DetalleCita.class))).thenAnswer(inv -> inv.getArgument(0));
        when(detalleCitaRepository.findByIdCita_IdCita(1)).thenReturn(List.of());

        detalleCitaService.actualizar(9, cambio);

        ArgumentCaptor<DetalleCita> captor = ArgumentCaptor.forClass(DetalleCita.class);
        verify(detalleCitaRepository).save(captor.capture());
        assertThat(captor.getValue().getPrecioAplicado()).isEqualByComparingTo("40");
        verify(medicamentosRepository, never()).findById(any());
    }

    // ---------- anular por cita ----------

    @Test
    void anularPorCita_devuelveElStockSoloDeLosActivos() {
        DetalleCita activo = detalleDeMedicamento(1, 2, "50", 3, true);
        DetalleCita yaAnulado = detalleDeMedicamento(2, 2, "50", 4, false);
        List<DetalleCita> detalles = List.of(activo, yaAnulado);
        Cita cita = citaActiva(1);
        when(detalleCitaRepository.findByIdCita_IdCita(1)).thenReturn(detalles);
        when(citaRepository.findById(1)).thenReturn(Optional.of(cita));

        detalleCitaService.anularPorCita(1);

        assertThat(activo.getEstado()).isFalse();
        verify(medicamentosService, times(1)).ajustarStock(2, 3);
        verify(medicamentosService, never()).ajustarStock(2, 4);
        verify(detalleCitaRepository).saveAll(detalles);
        assertThat(cita.getTotal()).isEqualByComparingTo("0");
    }
}
