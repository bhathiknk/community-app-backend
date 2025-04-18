package com.communityappbackend.config;

import com.communityappbackend.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class).build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Allow unauthenticated access to Swagger URLs
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        // Auth APIs (signup/login) can be accessed without token
                        .requestMatchers("/api/auth/**").permitAll()

                        // Profile images (upload requires auth, but the file path can be public)
                        .requestMatchers("/ProfileImages/**").permitAll()
                        .requestMatchers("/uploadProfileImage", "/getProfileImage").authenticated()
                        .requestMatchers("/image/{fileName}/**").permitAll()

                        // Items: /api/items/image is public (serves images), others require auth
                        .requestMatchers("/api/items/image/**").permitAll()
                        .requestMatchers("/api/items/**").authenticated()

                        // Donations: images are public, everything else requires auth
                        .requestMatchers("/api/donations/image/**").permitAll()
                        .requestMatchers("/api/donations/add").authenticated()
                        .requestMatchers("/api/donations/**").authenticated()

                        .requestMatchers("/api/ratings/**").authenticated()
                        .requestMatchers("/api/ratings/me/**").authenticated()
                        .requestMatchers("/api/ratings/me/detailed").authenticated()
                        .requestMatchers("/api/ratings/me/received").authenticated()



                        // user endpoints
                        .requestMatchers("/api/user/**").authenticated()

                        // all other requests must be authenticated
                        .anyRequest().authenticated()
                )
                // Insert our JWT filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
