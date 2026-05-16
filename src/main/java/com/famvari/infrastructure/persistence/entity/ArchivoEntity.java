package com.famvari.infrastructure.persistence.entity;

import java.time.LocalDateTime;
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

@Entity
@Table(name = "archivos")
public class ArchivoEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "archivosSeq")
    @SequenceGenerator(name = "archivosSeq", sequenceName = "archivos_SEQ", allocationSize = 1)
    public Long id;

    @Column(nullable = false)
    public String rutaArchivo;

    @Column(nullable = false)
    public String extension;

    @Column(nullable = false)
    public Boolean activo = true;

    @org.hibernate.annotations.CreationTimestamp
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    public LocalDateTime fechaRegistro;

    @Column(name = "fecha_eliminacion")
    public LocalDateTime fechaEliminacion;

    @OneToMany(mappedBy = "archivo")
    public Set<FileShareEntity> compartidosCon = new HashSet<>();
}
