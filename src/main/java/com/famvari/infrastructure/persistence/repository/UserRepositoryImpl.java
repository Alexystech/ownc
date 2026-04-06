package com.famvari.infrastructure.persistence.repository;

import com.famvari.domain.repository.UserRepositoryInterface;
import com.famvari.infrastructure.persistence.entity.UserEntity;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepositoryImpl implements UserRepositoryInterface, PanacheRepositoryBase<UserEntity, Long> {
    @Override
    public Uni<UserEntity> findByEmail(String email) {
        return find("email", email).firstResult();
    }
}
