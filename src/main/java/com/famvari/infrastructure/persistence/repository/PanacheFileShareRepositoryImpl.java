package com.famvari.infrastructure.persistence.repository;

import com.famvari.domain.repository.FileShareRepositoryInterface;
import com.famvari.infrastructure.persistence.entity.FileShareEntity;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class PanacheFileShareRepositoryImpl implements FileShareRepositoryInterface, PanacheRepositoryBase<FileShareEntity, Long> {

    @Override
    public Uni<FileShareEntity> save(FileShareEntity fileShare) {
        return persist(fileShare);
    }

    @Override
    public Uni<List<FileShareEntity>> findByUserEmail(String email) {
        return list("user.email", email);
    }

    @Override
    public Uni<FileShareEntity> findOwnerByArchivoId(Long archivoId) {
        return find("archivo.id = ?1 and permisoRol = 'Owner'", archivoId).firstResult();
    }
}
