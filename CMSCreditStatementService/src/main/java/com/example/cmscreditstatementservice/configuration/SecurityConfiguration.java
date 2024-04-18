package com.example.cmscreditstatementservice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class SecurityConfiguration {

    @Bean
    @Profile("default")
    SecurityFilterChain oauth2SecurityFilterChainDefault(HttpSecurity http) throws Exception {

        JwtAuthenticationConverter customJwtAuthenticationConverter = new JwtAuthenticationConverter();
        customJwtAuthenticationConverter
                .setJwtGrantedAuthoritiesConverter(new CustomJwtGrantedAuthoritiesConverter());

        http.authorizeHttpRequests(request -> request
                        .requestMatchers( "/h2-console/**", "/actuator/**")
                        .permitAll()
                        .anyRequest().authenticated()
                        )
                        .sessionManagement((session) -> session
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        )
                        .oauth2ResourceServer((resourceServer) -> resourceServer
                                .jwt(jwtConfigurer -> jwtConfigurer
                                        .jwtAuthenticationConverter(customJwtAuthenticationConverter)
                                )
                        );

        http.csrf(AbstractHttpConfigurer::disable);
        http.headers((headers) -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        return http.build();
    }

    @Bean
    @Profile({"dev", "prod"})
    SecurityFilterChain oauth2SecurityFilterChain(HttpSecurity http) throws Exception {

        JwtAuthenticationConverter customJwtAuthenticationConverter = new JwtAuthenticationConverter();
        customJwtAuthenticationConverter
                .setJwtGrantedAuthoritiesConverter(new CustomJwtGrantedAuthoritiesConverter());

        http.authorizeHttpRequests(request -> request
                        .requestMatchers("/actuator/**")
                        .permitAll()
                        .anyRequest().authenticated()
                        )
                        .sessionManagement((session) -> session
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        )
                        .oauth2ResourceServer((resourceServer) -> resourceServer
                            .jwt(jwtConfigurer -> jwtConfigurer
                                .jwtAuthenticationConverter(customJwtAuthenticationConverter)
                            )
                        );

        // Adding a CSRF token to requests from the backend to resource servers may not make sense
        // in a stateless context. CSRF tokens are primarily used to protect against attacks where an
        // unauthorized party leverages the authenticated state of a user's session in the browser to perform
        // actions on behalf of the user without their consent.
        http.csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }
}

