package com.famvari.rest;

import io.smallrye.mutiny.Uni;

import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

import com.famvari.application.service.FileAppService;
import com.famvari.domain.service.StorageService;
import com.famvari.rest.dto.ApiResponse;
import com.famvari.rest.dto.FileDetails;
import java.util.List;

import org.eclipse.microprofile.jwt.JsonWebToken;
import com.famvari.infrastructure.ratelimit.RedisRateLimit;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;

@Path("/storage")
@RolesAllowed("User")
public class UploadResource {

    @Inject
    JsonWebToken jwt;

    @Inject
    FileAppService fileAppService;

    @Inject
    StorageService storageService;

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @RedisRateLimit(limit = 5, windowSeconds = 60)
    public Uni<ApiResponse<FileDetails>> uploadFile(@RestForm("file") FileUpload file) {
        String email = jwt.getClaim("email");
        return fileAppService.uploadAndRegister(file.fileName(), file.contentType(), file.filePath().toFile(), email)
            .onItem().transform(path -> {
                FileDetails details = new FileDetails(file.fileName(), file.contentType(), path);
                return new ApiResponse<>(true, "Archivo subido exitosamente", List.of(details));
            })
            .onFailure().recoverWithItem(e -> new ApiResponse<>(false, "Error al subir el archivo: " + e.getMessage(), null));
    }

    // endpoint para listar las rutas publicas de archivos de storage por email de usuario
    @POST
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    @RedisRateLimit(limit = 20, windowSeconds = 60)
    public Uni<ApiResponse<FileDetails>> listFilesByEmail() {
        String email = jwt.getClaim("email");
        return storageService.listUserFiles(email)
            .onItem().transform(files -> new ApiResponse<>(true, "Archivos listados exitosamente", files))
            .onFailure().recoverWithItem(e -> 
                new ApiResponse<>(false, "Error al listar los archivos: " + e.getMessage(), null)
            );
    }
}
