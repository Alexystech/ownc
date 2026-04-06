package com.famvari.domain.repository;

import com.famvari.infrastructure.persistence.entity.ArchivoEntity;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public interface ArchivoRepositoryInterface {
    Uni<ArchivoEntity> save(ArchivoEntity archivo);
    Uni<ArchivoEntity> findById(Long id);
}