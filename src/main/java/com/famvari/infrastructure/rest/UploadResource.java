package com.famvari.infrastructure.rest;

import io.smallrye.mutiny.Uni;

import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

import com.famvari.domain.service.StorageService;
import com.famvari.infrastructure.rest.dto.ApiResponse;
import com.famvari.infrastructure.rest.dto.FileDetails;
import java.util.List;

import org.eclipse.microprofile.jwt.JsonWebToken;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;

@Path("/storage")
@RolesAllowed("User")
public class UploadResource {

    @Inject
    JsonWebToken jwt;

    @Inject
    StorageService storageService;

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<ApiResponse<FileDetails>> uploadFile(@RestForm("file") FileUpload file) {
        System.out.println("Usuario " + jwt.claim("email").get() + " subiendo archivo: " + file.fileName());
        return storageService.uploadFile(file.fileName(), file.contentType(), file.filePath().toFile())
                .map(name -> {
                    FileDetails details = new FileDetails(name);
                    return new ApiResponse<>(true, "File uploaded successfully", List.of(details));
                });
    }
}
