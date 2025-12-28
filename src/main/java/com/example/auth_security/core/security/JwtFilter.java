package com.example.auth_security.core.security;

import com.example.auth_security.common.exception.CommonErrorCode;
import com.example.auth_security.common.exception.CommonException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.example.auth_security.core.security.PublicUrlsConstants.PUBLIC_URLS;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(
            @NotNull

            final HttpServletRequest request,
            @NotNull
            final HttpServletResponse response,
            @NotNull
            final FilterChain filterChain) throws ServletException, IOException {

        final String jwt;
        final String username;

        // skip auth public paths from jwt filter
        if (isPublicPath(request)) {
            filterChain.doFilter(request, response);
            return;
        }


        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendError(response, "unauthorized", "Authorization header missing");
            return;
        }



        jwt = authHeader.substring(7);

        try {
            username = jwtService.extractUsername(jwt);
        } catch (RuntimeException ex) {
            if ("token_expired".equals(ex.getMessage())) {
                sendError(response, "token_expired", "Access token has expired");
                return;
            } else {
                sendError(response, "invalid_token", "Invalid access token");
                return;
            }
        }

        if (username == null) {
            sendError(response, "invalid_token", "Invalid access token");
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            final UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // check if expired
            if (jwtService.isTokenExpired(jwt)) {
                sendError(response, "token_expired", "Access token has expired");
                return;
            }

            // check if valid
            if (!jwtService.isTokenValid(jwt, userDetails.getUsername())) {
                sendError(response, "invalid_token", "Invalid access token");
                return;
            }

            final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities());

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authToken);

        }
        filterChain.doFilter(request, response);


    }


    // helper to send JSON error
    private void sendError(HttpServletResponse response, String error, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + error + "\",\"message\":\"" + message + "\"}");
    }


    private boolean isPublicPath(HttpServletRequest request) {
        AntPathMatcher matcher = new AntPathMatcher();
        String path = request.getServletPath();

        for (String pattern : PUBLIC_URLS) {
            if (matcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

}
