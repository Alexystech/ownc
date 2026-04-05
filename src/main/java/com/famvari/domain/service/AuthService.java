package com.famvari.domain.service;

import io.smallrye.mutiny.Uni;

public interface AuthService {
    /**
     * Verifica si el usuario existe y genera/envía un OTP.
     * Retorna true si el proceso se inició, false si no (por seguridad).
     */
    Uni<Boolean> requestOtp(String email);

    /**
     * Valida el código enviado por el usuario.
     * Retorna un Token JWT si es exitoso.
     */
    Uni<String> verifyOtp(String email, String code);
}