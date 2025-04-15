package com.communityappbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Exposes the ProfileImages folder as a static resource.
 */
@Configuration
public class ProfileImageResourceConfig implements WebMvcConfigurer {

    // Make sure this file system path exactly matches where your images are stored.
    private static final String UPLOAD_DIR =
            "C:/Projects/Community App/community-app-backend/src/main/java/com/communityappbackend/Assets/ProfileImages/";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/ProfileImages/**")
                .addResourceLocations("file:" + UPLOAD_DIR);
    }
}
