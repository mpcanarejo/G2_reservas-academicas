package com.grupo2.reservas.service;

import com.grupo2.reservas.entity.Usuario;
import com.grupo2.reservas.exception.RecursoNoEncontradoException;
import com.grupo2.reservas.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    // Inyección por constructor
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Usuario no encontrado con id " + id));
    }

    public Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Usuario actualizar(Long id, Usuario datos) {
        Usuario existente = obtenerPorId(id);
        existente.setNombre(datos.getNombre());
        existente.setCorreo(datos.getCorreo());
        existente.setRol(datos.getRol());
        return usuarioRepository.save(existente);
    }

    public void eliminar(Long id) {
        usuarioRepository.delete(obtenerPorId(id));
    }
}