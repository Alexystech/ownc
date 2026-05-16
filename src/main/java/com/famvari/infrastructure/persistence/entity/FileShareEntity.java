package com.famvari.infrastructure.persistence.entity;

import java.time.LocalDateTime;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "file_shares")
public class FileShareEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fileSharesSeq")
    @SequenceGenerator(name = "fileSharesSeq", sequenceName = "file_shares_SEQ", allocationSize = 1)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    public UserEntity user;

    @ManyToOne
    @JoinColumn(name = "archivo_id", nullable = false)
    public ArchivoEntity archivo;

    @org.hibernate.annotations.CreationTimestamp
    @Column(name = "fecha_alta", nullable = false, updatable = false)
    public LocalDateTime fechaAlta;

    @Column(name = "permiso_rol", nullable = false)
    public String permisoRol; // "Owner", "Editor", "Viewer", etc.
}
