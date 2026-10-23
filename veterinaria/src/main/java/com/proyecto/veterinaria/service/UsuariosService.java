package com.proyecto.veterinaria.service;

import com.proyecto.veterinaria.exception.RecursoNoEncontradoException;

import com.proyecto.veterinaria.dto.LoginRequestDTO;
import com.proyecto.veterinaria.dto.LoginResponseDTO;
import com.proyecto.veterinaria.dto.UsuariosDTO;
import com.proyecto.veterinaria.entity.Usuarios;
import com.proyecto.veterinaria.entity.Perfil;
import com.proyecto.veterinaria.repository.UsuariosRepository;
import com.proyecto.veterinaria.security.JwtService;
import com.proyecto.veterinaria.exception.CredencialesInvalidasException;
import com.proyecto.veterinaria.exception.ReglaNegocioException;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuariosService {

    private final UsuariosRepository usuariosRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public UsuariosService(UsuariosRepository usuariosRepository, JwtService jwtService,
            PasswordEncoder passwordEncoder) {
        this.usuariosRepository = usuariosRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    private boolean esHashBcrypt(String valor) {
        return valor != null && valor.startsWith("$2");
    }

    public List<UsuariosDTO> filtroActivos() {
        return usuariosRepository.findByEstadoTrueOrderByIdUsuarioDesc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public boolean existeUsuario(String usuario) {
        return usuariosRepository.existsByUsuario(usuario);
    }

    public boolean existeCorreo(String correo) {
        return usuariosRepository.existsByCorreo(correo);
    }

    public List<UsuariosDTO> findAll() {
        return usuariosRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public UsuariosDTO findById(Integer id) {
        Usuarios entity = usuariosRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        return convertToDTO(entity);
    }

    public UsuariosDTO agregar(UsuariosDTO dto) {
        if (dto.getContrasena() == null || dto.getContrasena().isBlank()) {
            throw new RuntimeException("La contraseña es obligatoria");
        }
        if (usuariosRepository.existsByUsuario(dto.getUsuario())) {
            throw new ReglaNegocioException("El nombre de usuario ya existe");
        }
        if (usuariosRepository.existsByCorreo(dto.getCorreo())) {
            throw new ReglaNegocioException("El correo ya está registrado");
        }
        Usuarios entity = convertToEntity(dto);
        entity.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        return convertToDTO(usuariosRepository.save(entity));
    }

    public UsuariosDTO modificar(Integer id, UsuariosDTO dto) {
        Usuarios entity = usuariosRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        entity.setNombre(dto.getNombre());
        entity.setUsuario(dto.getUsuario());
        if (dto.getContrasena() != null && !dto.getContrasena().isBlank()) {
            entity.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        }
        entity.setCorreo(dto.getCorreo());
        entity.setEstado(dto.getEstado());
        if (dto.getIdPerfil() != null)
            entity.setIdPerfil(new Perfil(dto.getIdPerfil()));

        return convertToDTO(usuariosRepository.save(entity));
    }

    public UsuariosDTO anular(Integer id) {
        Usuarios entity = usuariosRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        entity.setEstado(false);
        return convertToDTO(usuariosRepository.save(entity));
    }

    private Usuarios convertToEntity(UsuariosDTO dto) {
        Usuarios entity = new Usuarios();
        entity.setNombre(dto.getNombre());
        entity.setUsuario(dto.getUsuario());
        entity.setContrasena(dto.getContrasena());
        entity.setCorreo(dto.getCorreo());
        entity.setEstado(dto.getEstado());
        if (dto.getIdPerfil() != null)
            entity.setIdPerfil(new Perfil(dto.getIdPerfil()));
        return entity;
    }

    private UsuariosDTO convertToDTO(Usuarios entity) {
        UsuariosDTO dto = new UsuariosDTO();
        dto.setIdUsuario(entity.getIdUsuario());
        dto.setNombre(entity.getNombre());
        dto.setUsuario(entity.getUsuario());
        dto.setCorreo(entity.getCorreo());
        dto.setEstado(entity.getEstado());
        dto.setIdPerfil(entity.getIdPerfil() != null ? entity.getIdPerfil().getIdPerfil() : null);
        dto.setNombrePerfil(NombresUtil.perfil(entity.getIdPerfil()));
        return dto;
    }

    public LoginResponseDTO login(LoginRequestDTO dto) {
        Usuarios entity = usuariosRepository.findByUsuario(dto.getUsuario())
                .orElseThrow(() -> new CredencialesInvalidasException("Usuario o contraseña incorrectos"));

        String guardada = entity.getContrasena();
        boolean coincide;
        if (esHashBcrypt(guardada)) {
            coincide = passwordEncoder.matches(dto.getContrasena(), guardada);
        } else {
            // Contraseña antigua en texto plano: se valida y se migra a BCrypt
            coincide = guardada != null && guardada.equals(dto.getContrasena());
            if (coincide) {
                entity.setContrasena(passwordEncoder.encode(dto.getContrasena()));
                usuariosRepository.save(entity);
            }
        }
        if (!coincide) {
            throw new CredencialesInvalidasException("Usuario o contraseña incorrectos");
        }
        if (!Boolean.TRUE.equals(entity.getEstado())) {
            throw new CredencialesInvalidasException("El usuario está inactivo");
        }

        String token = jwtService.generarToken(
                entity.getUsuario(), entity.getIdUsuario(),
                entity.getIdPerfil().getIdPerfil(), entity.getIdPerfil().getNombrePerfil());

        LoginResponseDTO response = new LoginResponseDTO();
        response.setIdUsuario(entity.getIdUsuario());
        response.setNombre(entity.getNombre());
        response.setUsuario(entity.getUsuario());
        response.setCorreo(entity.getCorreo());
        response.setIdPerfil(entity.getIdPerfil().getIdPerfil());
        response.setNombrePerfil(entity.getIdPerfil().getNombrePerfil());
        response.setToken(token);
        return response;
    }
}