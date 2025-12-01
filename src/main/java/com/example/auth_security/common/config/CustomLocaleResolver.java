package com.example.auth_security.common.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.Nullable;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class CustomLocaleResolver implements LocaleResolver {


    private static final List<String> SUPPORTED_LANGUAGES = Arrays.asList("en", "ar");

    private Locale defaultLocale = Locale.ENGLISH;

    public CustomLocaleResolver() {}

    public CustomLocaleResolver(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        // check custom header
        String langHeader = request.getHeader("lang");
        if (langHeader != null && !langHeader.isEmpty()) {
            String code = extractSupportedLang(langHeader);
            if (code != null) return new Locale(code);
        }

        // fallback to Accept-Language header
        String acceptLang = request.getHeader("Accept-Language");
        if (acceptLang != null && !acceptLang.isEmpty()) {
            String code = extractSupportedLang(acceptLang.split(",")[0]); // first language
            if (code != null) return new Locale(code);
        }

        // check session
        Locale sessionLocale = (Locale) request.getSession().getAttribute("SESSION_LOCALE");
        if (sessionLocale != null) return sessionLocale;

        // default
        return defaultLocale;
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, @Nullable Locale locale) {
        if (locale != null) {
            request.getSession().setAttribute("SESSION_LOCALE", locale);
        }
    }

    private static String extractSupportedLang(String code) {
        if (code == null) return null;
        code = code.trim().toLowerCase();
        return SUPPORTED_LANGUAGES.contains(code) ? code : null;
    }


    public void setDefaultLocale(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }
}
