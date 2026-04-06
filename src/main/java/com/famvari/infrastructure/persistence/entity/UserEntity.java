package com.famvari.infrastructure.persistence.entity;

import java.util.HashSet;
import java.util.Set;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
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

    @Column(nullable = false)
    public String role; // "User", "Admin", etc.

    @OneToMany(mappedBy = "user")
    public Set<FileShareEntity> shares = new HashSet<>();

    // Método de utilidad para buscar por email de forma reactiva
    public static Uni<UserEntity> findByEmail(String email) {
        return find("email", email).firstResult();
    }
}