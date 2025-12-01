package com.example.auth_security.common.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.LocaleResolver;


import java.util.Locale;

@Configuration
public class LocaleConfig implements WebMvcConfigurer {

    @Bean
    public LocaleResolver localeResolver() {
        CustomLocaleResolver resolver = new CustomLocaleResolver();
        resolver.setDefaultLocale(Locale.ENGLISH);
        return resolver;
    }


    // MessageSource for loading messages from multiple modules
    @Bean
    public MessageSource messageSource() {

        final String basePath = "com/example/auth_security/";

        String[] messagesFilesPaths = {
            basePath + "common/messages/validation",
            basePath + "common/messages/common_messages",
            basePath + "user/messages/messages"
        };



        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames(messagesFilesPaths);

        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(3600); // cache for 1 hour
        messageSource.setFallbackToSystemLocale(false);
        return messageSource;

    }

}
