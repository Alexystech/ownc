package com.famvari.infrastructure.auth;

import io.smallrye.mutiny.Uni;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import com.famvari.domain.service.AuthService;
import com.famvari.infrastructure.persistence.entity.UserEntity;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.reactive.ReactiveMailer;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.value.ReactiveValueCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import io.smallrye.jwt.build.Jwt;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import java.util.HashSet;
import java.util.Arrays;
import java.time.Duration;
import java.util.Random;

@ApplicationScoped
public class AuthServiceImpl implements AuthService {

    @Inject
    ReactiveRedisDataSource reactiveRedisDataSource;

    @Inject
    ReactiveMailer reactiveMailer;

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;

    // Usamos la versión reactiva de los comandos de Redis
    private final ReactiveValueCommands<String, String> otpCommands;

    public AuthServiceImpl(ReactiveRedisDataSource ds) {
        this.otpCommands = ds.value(String.class);
    }

    @Override
    @WithSession
    public Uni<Boolean> requestOtp(String email) {
        return UserEntity.findByEmail(email)
            .onItem().transformToUni(user -> {
                if (user == null) {
                    return Uni.createFrom().item(false);
                }

                // Generamos el código
                String otpCode = String.format("%06d", new Random().nextInt(1000000));

                // ENCADENAMOS LAS OPERACIONES REACTIVAS
                // 1. Guardar en Redis
                return otpCommands.setex("otp:" + email, 300, otpCode)
                    // 2. Luego enviar el mail
                    .chain(() -> reactiveMailer.send(
                        Mail.withText(email, "Acceso OWNC", "Tu código es: " + otpCode)
                    ))
                    .replaceWith(true);
            });
    }

    @Override
    public Uni<String> verifyOtp(String email, String code) {
        // Redis Reactivo devuelve un Uni<String>
        return otpCommands.get("otp:" + email)
            .onItem().transformToUni(storedCode -> {
                if (storedCode != null && storedCode.equals(code)) {
                    // Borramos y generamos token
                    return otpCommands.getdel("otp:" + email)
                        .map(ignored -> generateJwt(email));
                }
                return Uni.createFrom().nullItem();
            });
    }

    private String generateJwt(String email) {
        return Jwt.issuer(issuer) 
                .upn(email) 
                .groups(new HashSet<>(Arrays.asList("User", "StorageAdmin"))) 
                .claim("email", email) 
                .expiresIn(Duration.ofHours(1)) 
                .sign();
    }
}