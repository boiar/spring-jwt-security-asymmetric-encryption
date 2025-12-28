package com.example.auth_security.core.security;

public class PublicUrlsConstants {

    private PublicUrlsConstants() {}

    public static final String[] PUBLIC_URLS = {
            "/api/v1/auth/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**"
    };

}
