package com.famvari.rest;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.famvari.domain.service.AuthService;
import com.famvari.infrastructure.ratelimit.RedisRateLimit;
import io.smallrye.mutiny.Uni;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    AuthService authService;

    @POST
    @Path("/request")
    @RedisRateLimit(limit = 5, windowSeconds = 60)
    public Uni<Response> requestOtp(OtpRequest request) {
        return authService.requestOtp(request.email)
                .onItem().transform(success -> {
                    if (success) {
                        return Response.ok("OTP enviado").build();
                    }
                    return Response.status(Response.Status.NOT_FOUND).entity("Usuario no registrado").build();
                });
    }

    @POST
    @Path("/verify")
    @RedisRateLimit(limit = 5, windowSeconds = 60)
    public Uni<Response> verifyOtp(VerifyRequest request) {
        return authService.verifyOtp(request.email, request.code)
                .onItem().ifNotNull().transform(token -> Response.ok(token).build())
                .onItem().ifNull().continueWith(() -> Response.status(Response.Status.UNAUTHORIZED).build());
    }

    @POST
    @Path("/logout")
    @RolesAllowed("User") // Solo usuarios logueados pueden desloguearse
    @RedisRateLimit(limit = 5, windowSeconds = 60)
    public Uni<Response> logout() {
        return Uni.createFrom().item(Response.noContent().build());
    }

    // DTOs para la petición y respuesta
    public static class VerifyRequest {
        public String email;
        public String code;
    }

    public static class OtpRequest {
        public String email;
    }

    public static class TokenResponse {
        public String token;

        public TokenResponse(String t) {
            this.token = t;
        }
    }

    public static class LoginRequest {
        public String email;
    }
}