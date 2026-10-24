package com.proyecto.veterinaria.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.proyecto.veterinaria.dto.LoginRequestDTO;
import com.proyecto.veterinaria.dto.LoginResponseDTO;
import com.proyecto.veterinaria.dto.UsuariosDTO;
import com.proyecto.veterinaria.entity.Perfil;
import com.proyecto.veterinaria.entity.Usuarios;
import com.proyecto.veterinaria.exception.CredencialesInvalidasException;
import com.proyecto.veterinaria.exception.IntentosExcedidosException;
import com.proyecto.veterinaria.exception.ReglaNegocioException;
import com.proyecto.veterinaria.repository.UsuariosRepository;
import com.proyecto.veterinaria.security.JwtService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UsuariosServiceTest {

    private static final String IP = "10.0.0.1";

    @Mock
    private UsuariosRepository usuariosRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private HistorialService historialService;

    private UsuariosService usuariosService;

    @BeforeEach
    void preparar() {
        usuariosService = new UsuariosService(usuariosRepository, jwtService, passwordEncoder,
                new LoginAttemptService(), historialService);
    }

    private static UsuariosDTO usuarioDto() {
        UsuariosDTO dto = new UsuariosDTO();
        dto.setNombre("Ana Pérez");
        dto.setUsuario("ana");
        dto.setContrasena("secreto123");
        dto.setCorreo("ana@correo.com");
        dto.setEstado(true);
        dto.setIdPerfil(1);
        return dto;
    }

    private static Usuarios usuarioGuardado(String contrasena, boolean activo) {
        Perfil perfil = new Perfil(1);
        perfil.setNombrePerfil("Administrador");
        Usuarios u = new Usuarios(5);
        u.setNombre("Ana Pérez");
        u.setUsuario("ana");
        u.setCorreo("ana@correo.com");
        u.setContrasena(contrasena);
        u.setEstado(activo);
        u.setIdPerfil(perfil);
        return u;
    }

    private static LoginRequestDTO login(String usuario, String contrasena) {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setUsuario(usuario);
        dto.setContrasena(contrasena);
        return dto;
    }

    // ---------- agregar / modificar ----------

    @Test
    void agregar_guardaLaContrasenaEncriptada() {
        when(passwordEncoder.encode("secreto123")).thenReturn("$2a$hash");
        when(usuariosRepository.save(any(Usuarios.class))).thenAnswer(inv -> inv.getArgument(0));

        usuariosService.agregar(usuarioDto());

        ArgumentCaptor<Usuarios> captor = ArgumentCaptor.forClass(Usuarios.class);
        verify(usuariosRepository).save(captor.capture());
        assertThat(captor.getValue().getContrasena()).isEqualTo("$2a$hash");
    }

    @Test
    void agregar_sinContrasena_lanzaArgumentoInvalido() {
        UsuariosDTO dto = usuarioDto();
        dto.setContrasena(" ");

        assertThatThrownBy(() -> usuariosService.agregar(dto))
                .isInstanceOf(IllegalArgumentException.class);
        verify(usuariosRepository, never()).save(any(Usuarios.class));
    }

    @Test
    void agregar_conUsuarioRepetido_lanzaReglaNegocio() {
        when(usuariosRepository.existsByUsuario("ana")).thenReturn(true);

        assertThatThrownBy(() -> usuariosService.agregar(usuarioDto()))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("usuario");
        verify(usuariosRepository, never()).save(any(Usuarios.class));
    }

    @Test
    void agregar_conCorreoRepetido_lanzaReglaNegocio() {
        when(usuariosRepository.existsByCorreo("ana@correo.com")).thenReturn(true);

        assertThatThrownBy(() -> usuariosService.agregar(usuarioDto()))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("correo");
    }

    @Test
    void modificar_conUsuarioDeOtraPersona_lanzaReglaNegocio() {
        when(usuariosRepository.findById(5)).thenReturn(Optional.of(usuarioGuardado("$2a$x", true)));
        when(usuariosRepository.existsByUsuarioAndIdUsuarioNot("ana", 5)).thenReturn(true);

        assertThatThrownBy(() -> usuariosService.modificar(5, usuarioDto()))
                .isInstanceOf(ReglaNegocioException.class);
        verify(usuariosRepository, never()).save(any(Usuarios.class));
    }

    @Test
    void modificar_conCorreoDeOtraPersona_lanzaReglaNegocio() {
        when(usuariosRepository.findById(5)).thenReturn(Optional.of(usuarioGuardado("$2a$x", true)));
        when(usuariosRepository.existsByCorreoAndIdUsuarioNot("ana@correo.com", 5)).thenReturn(true);

        assertThatThrownBy(() -> usuariosService.modificar(5, usuarioDto()))
                .isInstanceOf(ReglaNegocioException.class);
    }

    @Test
    void modificar_sinContrasenaNueva_conservaLaActual() {
        Usuarios existente = usuarioGuardado("$2a$actual", true);
        when(usuariosRepository.findById(5)).thenReturn(Optional.of(existente));
        when(usuariosRepository.save(any(Usuarios.class))).thenAnswer(inv -> inv.getArgument(0));
        UsuariosDTO dto = usuarioDto();
        dto.setContrasena(null);

        usuariosService.modificar(5, dto);

        assertThat(existente.getContrasena()).isEqualTo("$2a$actual");
        verify(passwordEncoder, never()).encode(any());
    }

    // ---------- login ----------

    @Test
    void login_correcto_devuelveElToken() {
        when(usuariosRepository.findByUsuario("ana")).thenReturn(Optional.of(usuarioGuardado("$2a$hash", true)));
        when(passwordEncoder.matches("clave1234", "$2a$hash")).thenReturn(true);
        when(jwtService.generarToken("ana", 5, 1, "Administrador")).thenReturn("token-jwt");

        LoginResponseDTO respuesta = usuariosService.login(login("ana", "clave1234"), IP);

        assertThat(respuesta.getToken()).isEqualTo("token-jwt");
        assertThat(respuesta.getNombrePerfil()).isEqualTo("Administrador");
    }

    @Test
    void login_conContrasenaIncorrecta_lanzaCredencialesInvalidas() {
        when(usuariosRepository.findByUsuario("ana")).thenReturn(Optional.of(usuarioGuardado("$2a$hash", true)));
        when(passwordEncoder.matches("mala", "$2a$hash")).thenReturn(false);

        assertThatThrownBy(() -> usuariosService.login(login("ana", "mala"), IP))
                .isInstanceOf(CredencialesInvalidasException.class);
    }

    @Test
    void login_conUsuarioInexistente_lanzaCredencialesInvalidas() {
        when(usuariosRepository.findByUsuario("nadie")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuariosService.login(login("nadie", "x"), IP))
                .isInstanceOf(CredencialesInvalidasException.class);
    }

    @Test
    void login_conUsuarioInactivo_lanzaCredencialesInvalidas() {
        when(usuariosRepository.findByUsuario("ana")).thenReturn(Optional.of(usuarioGuardado("$2a$hash", false)));
        when(passwordEncoder.matches("clave1234", "$2a$hash")).thenReturn(true);

        assertThatThrownBy(() -> usuariosService.login(login("ana", "clave1234"), IP))
                .isInstanceOf(CredencialesInvalidasException.class)
                .hasMessageContaining("inactivo");
    }

    @Test
    void login_conContrasenaEnTextoPlano_laMigraABcrypt() {
        Usuarios existente = usuarioGuardado("plana", true);
        when(usuariosRepository.findByUsuario("ana")).thenReturn(Optional.of(existente));
        when(passwordEncoder.encode("plana")).thenReturn("$2a$nuevo");

        usuariosService.login(login("ana", "plana"), IP);

        assertThat(existente.getContrasena()).isEqualTo("$2a$nuevo");
        verify(usuariosRepository).save(existente);
    }

    @Test
    void login_tras5Fallos_bloqueaElSiguienteIntento() {
        when(usuariosRepository.findByUsuario("ana")).thenReturn(Optional.empty());

        for (int i = 0; i < LoginAttemptService.MAX_INTENTOS; i++) {
            assertThatThrownBy(() -> usuariosService.login(login("ana", "x"), IP))
                    .isInstanceOf(CredencialesInvalidasException.class);
        }

        assertThatThrownBy(() -> usuariosService.login(login("ana", "x"), IP))
                .isInstanceOf(IntentosExcedidosException.class);
    }
}
