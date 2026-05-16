package com.famvari.infrastructure.ratelimit;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.value.ReactiveValueCommands;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;
import io.vertx.core.http.HttpServerRequest;

import com.famvari.rest.dto.ApiResponse;

import java.time.Duration;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RedisRateLimit
public class RedisRateLimitFilter {

    @Context
    ResourceInfo resourceInfo;

    @Inject
    JsonWebToken jwt;

    @Inject
    ReactiveRedisDataSource reactiveRedisDataSource;

    @Inject
    HttpServerRequest request;

    @ServerRequestFilter
    public Uni<Response> filter() {
        RedisRateLimit annotation = resourceInfo.getResourceMethod().getAnnotation(RedisRateLimit.class);
        if (annotation == null) {
            annotation = resourceInfo.getResourceClass().getAnnotation(RedisRateLimit.class);
        }

        if (annotation == null) {
            return Uni.createFrom().nullItem();
        }

        String email = jwt.getClaim("email");
        String identifier = (email != null) ? email : request.remoteAddress().host();
        if (identifier == null)
            identifier = "anonymous";

        String key = "rl:" + resourceInfo.getResourceMethod().getName() + ":" + identifier;
        int limit = annotation.limit();
        int window = annotation.windowSeconds();

        ReactiveValueCommands<String, Long> valueCommands = reactiveRedisDataSource.value(Long.class);

        return valueCommands.get(key)
                .onItem().<Response>transformToUni(count -> {
                    if (count != null && count >= limit) {
                        ApiResponse<Void> errorResponse = new ApiResponse<>(
                                false,
                                "Demasiadas peticiones. Límite: " + limit + " por cada " + window + "s.",
                                null);
                        return Uni.createFrom().item(Response.status(429)
                                .entity(errorResponse)
                                .build());
                    }

                    return valueCommands.incr(key)
                            .onItem().<Response>transformToUni(newCount -> {
                                if (newCount == 1) {
                                    return reactiveRedisDataSource.key().expire(key, Duration.ofSeconds(window))
                                            .map(v -> (Response) null);
                                }
                                return Uni.createFrom().item((Response) null);
                            });
                });
    }
}
