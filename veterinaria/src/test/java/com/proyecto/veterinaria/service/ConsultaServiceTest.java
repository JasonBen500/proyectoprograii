package com.proyecto.veterinaria.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.proyecto.veterinaria.dto.ConsultaDTO;
import com.proyecto.veterinaria.entity.Cita;
import com.proyecto.veterinaria.entity.Consulta;
import com.proyecto.veterinaria.exception.RecursoNoEncontradoException;
import com.proyecto.veterinaria.repository.ConsultaRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConsultaServiceTest {

    @Mock
    private ConsultaRepository consultaRepository;

    @InjectMocks
    private ConsultaService consultaService;

        @Mock
    private HistorialService historialService;

    private static ConsultaDTO consultaDto() {
        ConsultaDTO dto = new ConsultaDTO();
        dto.setDiagnostico("Otitis");
        dto.setSintomas("Picazón en el oído");
        dto.setEstado(true);
        dto.setIdCita(4);
        return dto;
    }

    private static Consulta consulta(Integer id) {
        Consulta c = new Consulta(id);
        c.setDiagnostico("Gripe");
        c.setSintomas("Tos");
        c.setEstado(true);
        c.setIdCita(new Cita(4));
        return c;
    }

    @Test
    void crear_guardaYDevuelveElDto() {
        when(consultaRepository.save(any(Consulta.class))).thenAnswer(inv -> {
            Consulta c = inv.getArgument(0);
            c.setIdConsulta(8);
            return c;
        });

        ConsultaDTO resultado = consultaService.crear(consultaDto());

        assertThat(resultado.getIdConsulta()).isEqualTo(8);
        assertThat(resultado.getDiagnostico()).isEqualTo("Otitis");
        assertThat(resultado.getIdCita()).isEqualTo(4);
    }

    @Test
    void findById_inexistente_lanzaNoEncontrado() {
        when(consultaRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> consultaService.findById(1))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void actualizar_cambiaDiagnosticoYSintomas() {
        Consulta existente = consulta(5);
        when(consultaRepository.findById(5)).thenReturn(Optional.of(existente));
        when(consultaRepository.save(any(Consulta.class))).thenAnswer(inv -> inv.getArgument(0));

        consultaService.actualizar(5, consultaDto());

        ArgumentCaptor<Consulta> captor = ArgumentCaptor.forClass(Consulta.class);
        verify(consultaRepository).save(captor.capture());
        assertThat(captor.getValue().getDiagnostico()).isEqualTo("Otitis");
        assertThat(captor.getValue().getSintomas()).isEqualTo("Picazón en el oído");
    }

    @Test
    void actualizar_inexistente_lanzaNoEncontrado() {
        when(consultaRepository.findById(5)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> consultaService.actualizar(5, consultaDto()))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void anular_marcaLaConsultaComoInactiva() {
        Consulta existente = consulta(6);
        when(consultaRepository.findById(6)).thenReturn(Optional.of(existente));
        when(consultaRepository.save(any(Consulta.class))).thenAnswer(inv -> inv.getArgument(0));

        ConsultaDTO resultado = consultaService.anular(6);

        assertThat(resultado.getEstado()).isFalse();
    }

    @Test
    void buscarPorDiagnostico_conviertePorCadaResultado() {
        when(consultaRepository.findByDiagnosticoContainingIgnoreCase("gri"))
                .thenReturn(List.of(consulta(1), consulta(2)));

        List<ConsultaDTO> resultado = consultaService.buscarPorDiagnostico("gri");

        assertThat(resultado).hasSize(2);
    }

    @Test
    void findActivas_devuelveSoloLoQueDaElRepositorio() {
        when(consultaRepository.findByEstadoTrueOrderByIdConsultaDesc()).thenReturn(List.of(consulta(3)));

        assertThat(consultaService.findActivas()).extracting(ConsultaDTO::getIdConsulta).containsExactly(3);
    }
}
