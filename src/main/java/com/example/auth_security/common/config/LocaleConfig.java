package com.example.auth_security.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.LocaleResolver;


import java.io.IOException;
import java.sql.Array;
import java.util.*;

@Configuration
@Slf4j
public class LocaleConfig implements WebMvcConfigurer {

    private final LanguageProperties languageProperties;

    public LocaleConfig(LanguageProperties languageProperties) {
        this.languageProperties = languageProperties;
    }


    @Bean
    public LocaleResolver localeResolver() {
        CustomLocaleResolver resolver = new CustomLocaleResolver(languageProperties);
        return resolver;
    }

    /**
     * Extended MessageSource to allow reading all merged messages
     */
    public static class ExtendedMessageSource extends ReloadableResourceBundleMessageSource {
        public Properties getAllMessages(Locale locale) {
            return super.getMergedProperties(locale).getProperties();
        }
    }


    // MessageSource for loading messages from multiple modules
    @Bean
    public MessageSource messageSource() throws IOException {


        ExtendedMessageSource messageSource = new ExtendedMessageSource();

        // auto-scanned messages files
        String[] baseNames = scanMessageFiles();
        messageSource.setBasenames(baseNames);
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(languageProperties.isDebug() ? -1 : languageProperties.getCacheSeconds());
        return messageSource;

    }


    /**
     * Auto-scan all *-messages*.properties files under messages folder
     */
    private String[] scanMessageFiles() throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath*:messages/*-messages*.properties");
        Set<String> baseNames = new HashSet<>();

        for (Resource resource : resources) {
            String filename = resource.getFilename();
            if (filename == null) continue;

            String nameWithoutExt = filename.replace(".properties", "");
            String baseName = nameWithoutExt.replaceAll("_[a-z]{2}(_[A-Z]{2})?$", "");

            baseNames.add("classpath:messages/" + baseName);
        }

        return baseNames.toArray(new String[0]);
    }


    public void logAllMessages(ExtendedMessageSource messageSource, Locale locale) {
        Properties allMessages = messageSource.getAllMessages(locale);
        log.info("====== All localized messages ({}) ======", locale);
        allMessages.forEach((key, value) -> log.info("{} = {}", key, value));
        log.info("========================================");
    }

}
