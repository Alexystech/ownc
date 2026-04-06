package com.famvari.domain.repository;

import com.famvari.infrastructure.persistence.entity.FileShareEntity;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public interface FileShareRepositoryInterface {
    
    // Guarda la relación (pivote) entre el usuario y el archivo
    Uni<FileShareEntity> save(FileShareEntity fileShare);

    // Opcional: Buscar todos los archivos compartidos con un usuario específico
    Uni<List<FileShareEntity>> findByUserEmail(String email);
    
    // Opcional: Buscar quién es el dueño de un archivo
    Uni<FileShareEntity> findOwnerByArchivoId(Long archivoId);
}