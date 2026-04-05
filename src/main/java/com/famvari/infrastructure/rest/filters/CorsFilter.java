package com.famvari.infrastructure.rest.filters;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class CorsFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, 
                       ContainerResponseContext responseContext) throws IOException {
        
        String origin = requestContext.getHeaderString("Origin");

        // Verificamos si el origen es tu subdominio exacto
        if (origin != null && origin.equals("https://ownc.famvari.com")) {
            responseContext.getHeaders().putSingle("Access-Control-Allow-Origin", "https://ownc.famvari.com");
        } else {
            // Para pruebas, si no es el subdominio, dejamos el de desarrollo
            responseContext.getHeaders().putSingle("Access-Control-Allow-Origin", "http://localhost:5173");
        }

        responseContext.getHeaders().putSingle("Access-Control-Allow-Headers", "origin, content-type, accept, authorization, x-requested-with");
        responseContext.getHeaders().putSingle("Access-Control-Allow-Credentials", "true");
        responseContext.getHeaders().putSingle("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        
        if (requestContext.getMethod().equalsIgnoreCase("OPTIONS")) {
            responseContext.setStatus(200);
        }
    }
}
