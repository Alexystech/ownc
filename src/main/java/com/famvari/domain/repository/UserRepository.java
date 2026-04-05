package com.famvari.domain.repository;

import com.famvari.infrastructure.persistence.entity.UserEntity;
import java.util.Optional;

public interface UserRepository {
    Optional<UserEntity> findByEmail(String email);
}