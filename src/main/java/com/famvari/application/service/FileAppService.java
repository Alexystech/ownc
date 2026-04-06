package com.famvari.application.service;

import com.famvari.domain.repository.ArchivoRepositoryInterface;
import com.famvari.domain.repository.FileShareRepositoryInterface;
import com.famvari.domain.repository.UserRepositoryInterface;
import com.famvari.domain.service.StorageService;
import com.famvari.infrastructure.persistence.entity.ArchivoEntity;
import com.famvari.infrastructure.persistence.entity.FileShareEntity;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.File;

@ApplicationScoped
public class FileAppService {

    @Inject
    StorageService storageService; // Interfaz en domain

    @Inject
    ArchivoRepositoryInterface archivoRepository; // Interfaz en domain

    @Inject
    FileShareRepositoryInterface shareRepository; // Interfaz en domain

    @Inject
    UserRepositoryInterface userRepository; // Interfaz en domain

    @WithTransaction
    public Uni<String> uploadAndRegister(String fileName, String contentType, File file, String email) {
        // 1. Subir a Google Cloud Storage
        String fullPath = email + File.separator + fileName;
        return storageService.uploadFile(fullPath, contentType, file)
            .chain(storagePath -> userRepository.findByEmail(email)
                .onItem().ifNull().failWith(() -> new RuntimeException("Usuario no encontrado"))
                .chain(user -> {
                    // 2. Crear registro de archivo
                    ArchivoEntity archivo = new ArchivoEntity();
                    archivo.rutaArchivo = storagePath;
                    archivo.extension = extractExtension(fileName);
                    archivo.activo = true;

                    return archivoRepository.save(archivo)
                        .chain(arc -> {
                            // 3. Crear relación de dueño (Many-to-Many pivote)
                            FileShareEntity share = new FileShareEntity();
                            share.user = user;
                            share.archivo = arc;
                            share.permisoRol = "Owner";
                            return shareRepository.save(share);
                        })
                        .replaceWith(storagePath);
                })
            );
    }

    private String extractExtension(String filename) {
        return filename.contains(".") ? filename.substring(filename.lastIndexOf(".") + 1) : "";
    }
}