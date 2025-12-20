package com.example.auth_security.core.lang;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.Nullable;
import org.springframework.web.servlet.LocaleResolver;

import java.util.List;
import java.util.Locale;

public class CustomLocaleResolver implements LocaleResolver {

    private static final String SESSION_LOCALE_ATTRIBUTE = "SESSION_LOCALE";
    private static final String LANG_HEADER = "lang";
    private static final String ACCEPT_LANGUAGE_HEADER = "Accept-Language";

    private final LanguageProperties langProps;

    public CustomLocaleResolver(LanguageProperties langProps) {
        this.langProps = langProps;
    }


    @Override
    public Locale resolveLocale(HttpServletRequest request) {

        // check custom header
        String langHeader = request.getHeader(LANG_HEADER);
        if (langHeader != null && !langHeader.isEmpty()) {
            String code = extractSupportedLang(langHeader);
            if (code != null) return Locale.forLanguageTag(code);
        }

        // fallback to Accept-Language header
        String acceptLang = request.getHeader(ACCEPT_LANGUAGE_HEADER);
        if (acceptLang != null && !acceptLang.isEmpty()) {
            String code = extractSupportedLang(acceptLang.split(",")[0]); // first language

            if (code != null) return Locale.forLanguageTag(code);
        }

        // check session
        Locale sessionLocale = (Locale) request.getSession().getAttribute(SESSION_LOCALE_ATTRIBUTE);
        if (sessionLocale != null) return sessionLocale;

        // default
        return langProps.getDefaultLocale();
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, @Nullable Locale locale) {
        if (locale != null) {
            request.getSession().setAttribute(SESSION_LOCALE_ATTRIBUTE, locale);
        }
    }

    private  String extractSupportedLang(String code) {
        if (code == null) return null;
        code = code.trim().toLowerCase();
        List<String> supported = langProps.getSupportedLanguages();

        return (supported != null && supported.contains(code)) ? code : null;
    }


}
