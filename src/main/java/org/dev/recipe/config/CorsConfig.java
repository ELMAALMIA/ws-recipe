package org.dev.recipe.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*") // Allow all origins
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allow all these HTTP methods
                .allowedHeaders("*") // Allow all headers
                .allowCredentials(false) // Credentials shouldn't be used with "*" as origin
                .maxAge(3600); // Cache preflight requests for 1 hour
    }
}
