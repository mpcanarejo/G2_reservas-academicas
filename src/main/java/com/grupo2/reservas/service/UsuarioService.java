package com.grupo2.reservas.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupo2.reservas.dto.UsuarioDTO;
import com.grupo2.reservas.entity.Usuario;
import com.grupo2.reservas.exception.DatosInvalidosException;
import com.grupo2.reservas.exception.RecursoNoEncontradoException;
import com.grupo2.reservas.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO> obtenerTodos() {
        return usuarioRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioDTO obtenerPorId(Long id) {
        return toDTO(buscar(id));
    }

    @Transactional
    public UsuarioDTO guardar(UsuarioDTO dto) {
        String correo = dto.getCorreo().trim().toLowerCase();
        if (usuarioRepository.existsByCorreoIgnoreCase(correo)) {
            throw new DatosInvalidosException("Ya existe un usuario con el correo: " + dto.getCorreo());
        }
        Usuario usuario = new Usuario();
        copiar(dto, usuario);
        return toDTO(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioDTO actualizar(Long id, UsuarioDTO dto) {
        Usuario usuario = buscar(id);
        String correo = dto.getCorreo().trim().toLowerCase();
        if (usuarioRepository.existsByCorreoIgnoreCaseAndIdNot(correo, id)) {
            throw new DatosInvalidosException("Ya existe otro usuario con el correo: " + dto.getCorreo());
        }
        copiar(dto, usuario);
        return toDTO(usuarioRepository.save(usuario));
    }

    @Transactional
    public void eliminar(Long id) {
        usuarioRepository.delete(buscar(id));
    }

    private Usuario buscar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con id " + id));
    }

    private void copiar(UsuarioDTO dto, Usuario usuario) {
        usuario.setNombre(dto.getNombre().trim());
        usuario.setCorreo(dto.getCorreo().trim().toLowerCase());
        usuario.setRol(dto.getRol().trim().toUpperCase());
    }

    private UsuarioDTO toDTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setCorreo(usuario.getCorreo());
        dto.setRol(usuario.getRol());
        return dto;
    }
}
