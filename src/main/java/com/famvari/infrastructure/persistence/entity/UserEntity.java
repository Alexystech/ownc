package com.famvari.infrastructure.persistence.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase; // Asegúrate que sea Base
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue; // Faltaba este
import jakarta.persistence.GenerationType; // Faltaba este
import jakarta.persistence.Id;             // Faltaba este
import jakarta.persistence.SequenceGenerator; // Faltaba este
import jakarta.persistence.Table;
import io.smallrye.mutiny.Uni;

@Entity
@Table(name = "users")
public class UserEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usersSeq")
    @SequenceGenerator(name = "usersSeq", sequenceName = "users_SEQ", allocationSize = 1)
    public Long id;

    @Column(unique = true, nullable = false)
    public String email;

    @Column(nullable = false)
    public String name;

    public String role; // "User", "Admin", etc.

    // Método de utilidad para buscar por email de forma reactiva
    public static Uni<UserEntity> findByEmail(String email) {
        return find("email", email).firstResult();
    }
}