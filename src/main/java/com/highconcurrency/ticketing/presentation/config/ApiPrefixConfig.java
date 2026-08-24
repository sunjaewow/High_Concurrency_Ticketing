package com.highconcurrency.ticketing.presentation.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiPrefixConfig implements WebMvcConfigurer {

    private static final String PREFIX = "v1";
    private static final String BASE_PATH = "com.highconcurrency.ticketing.presentation.controller";

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(PREFIX, HandlerTypePredicate.forBasePackage(BASE_PATH));
    }
}
