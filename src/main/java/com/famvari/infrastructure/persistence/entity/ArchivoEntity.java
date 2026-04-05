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
import jakarta.persistence.ManyToMany;
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
    
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    public Boolean activo;
    
    @Column(name = "fecha_registro", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", updatable = false, insertable = false)
    public LocalDateTime fechaRegistro;
    
    @Column(name = "fecha_eliminacion")
    public LocalDateTime fechaEliminacion;

    @ManyToMany(mappedBy = "archivos")
    public Set<UserEntity> usuarios = new HashSet<>();
}
