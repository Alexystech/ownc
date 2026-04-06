package com.famvari.infrastructure.persistence.repository;

import com.famvari.domain.repository.ArchivoRepositoryInterface;
import com.famvari.infrastructure.persistence.entity.ArchivoEntity;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ArchivoRepositoryImpl implements ArchivoRepositoryInterface, PanacheRepositoryBase<ArchivoEntity, Long> {
    @Override
    public Uni<ArchivoEntity> save(ArchivoEntity archivo) {
        return persist(archivo);
    }

    @Override
    public Uni<ArchivoEntity> findById(Long id) {
        // Llamamos al findById que ya viene en PanacheRepositoryBase
        return PanacheRepositoryBase.super.findById(id);
    }
}
