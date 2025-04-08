package com.communityappbackend.Config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import org.springframework.context.annotation.*;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Community App API")
                        .version("1.0")
                        .description("API documentation for the Community Sharing Application")
                        .contact(new Contact()
                                .name("Your Name")
                                .email("your.email@example.com")
                        )
                );
    }
}
