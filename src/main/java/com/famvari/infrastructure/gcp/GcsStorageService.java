// src/main/java/com/famvari/infrastructure/gcp/GcsStorageService.java
package com.famvari.infrastructure.gcp;

import com.famvari.domain.service.StorageService;
import com.famvari.infrastructure.rest.dto.FileDetails;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@ApplicationScoped
public class GcsStorageService implements StorageService {

    private Storage storage;

    @ConfigProperty(name = "google.cloud.project-id")
    String projectId;

    @ConfigProperty(name = "google.cloud.credentials.file-path", defaultValue = "default")
    String jsonPath;

    @ConfigProperty(name = "mi.bucket.name")
    String bucketName;

    @PostConstruct
    void init() throws IOException {
        File jsonFile = new File(jsonPath);
        if (jsonFile.exists()) {
            this.storage = StorageOptions.newBuilder()
                    .setProjectId(projectId)
                    .setCredentials(GoogleCredentials.fromStream(new FileInputStream(jsonPath)))
                    .build()
                    .getService();
        } else {
            this.storage = StorageOptions.getDefaultInstance().getService();
        }
    }

    @Override
    public Uni<String> uploadFile(String fileName, String contentType, File file) {
        return Uni.createFrom().item(() -> {
            try {
                BlobId blobId = BlobId.of(bucketName, fileName);
                BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(contentType).build();
                storage.create(blobInfo, Files.readAllBytes(file.toPath()));
                return fileName;
            } catch (Exception e) {
                throw new RuntimeException("Error subiendo a GCS", e);
            }
        });
    }

    @Override
    public Uni<List<FileDetails>> listFiles() {
        return Uni.createFrom().item(() -> {
            var blobs = storage.list(bucketName).iterateAll();
            return StreamSupport.stream(blobs.spliterator(), false)
                    .map(blob -> new FileDetails(blob.getName()))
                    .collect(Collectors.toList());
        });
    }
}