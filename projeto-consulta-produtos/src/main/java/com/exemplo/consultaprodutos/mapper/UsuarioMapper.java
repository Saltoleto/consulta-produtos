package com.exemplo.consultaprodutos.mapper;

import com.exemplo.consultaprodutos.dto.request.UsuarioRequestDTO;
import com.exemplo.consultaprodutos.dto.response.UsuarioResponseDTO;
import com.exemplo.consultaprodutos.entity.Usuario;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para conversões entre Usuario e seus DTOs.
 */
@Component
public class UsuarioMapper {

    /**
     * Converte UsuarioRequestDTO para Usuario (para criação).
     */
    public Usuario toEntity(UsuarioRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setTelefone(dto.getTelefone());
        
        return usuario;
    }

    /**
     * Atualiza entidade Usuario com dados do UsuarioRequestDTO.
     */
    public void updateEntity(Usuario usuario, UsuarioRequestDTO dto) {
        if (usuario == null || dto == null) {
            return;
        }

        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setTelefone(dto.getTelefone());
    }

    /**
     * Converte Usuario para UsuarioResponseDTO (básico).
     */
    public UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setNome(usuario.getNome());
        dto.setEmail(usuario.getEmail());
        dto.setTelefone(usuario.getTelefone());
        dto.setDataCriacao(usuario.getDataCriacao());
        dto.setDataAtualizacao(usuario.getDataAtualizacao());
        dto.setAtivo(usuario.getAtivo());
        
        return dto;
    }

    /**
     * Converte Usuario para UsuarioResponseDTO (com total de produtos).
     */
    public UsuarioResponseDTO toResponseDTO(Usuario usuario, Long totalProdutos) {
        UsuarioResponseDTO dto = toResponseDTO(usuario);
        if (dto != null) {
            dto.setTotalProdutos(totalProdutos);
        }
        return dto;
    }

    /**
     * Converte Usuario para UsuarioResponseDTO (resumido, sem dados de auditoria).
     */
    public UsuarioResponseDTO toResponseDTOResumo(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setNome(usuario.getNome());
        dto.setEmail(usuario.getEmail());
        dto.setTelefone(usuario.getTelefone());
        
        return dto;
    }

    /**
     * Converte lista de Usuario para lista de UsuarioResponseDTO.
     */
    public List<UsuarioResponseDTO> toResponseDTOList(List<Usuario> usuarios) {
        if (usuarios == null) {
            return null;
        }

        return usuarios.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Converte lista de Usuario para lista de UsuarioResponseDTO (resumido).
     */
    public List<UsuarioResponseDTO> toResponseDTOResumoList(List<Usuario> usuarios) {
        if (usuarios == null) {
            return null;
        }

        return usuarios.stream()
                .map(this::toResponseDTOResumo)
                .collect(Collectors.toList());
    }
}

