package com.famvari.domain.repository;

import com.famvari.infrastructure.persistence.entity.UserEntity;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public interface UserRepositoryInterface {
    Uni<UserEntity> findByEmail(String email);
}