package com.example.cmsauthservice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration {

    @Bean
    SecurityFilterChain oauth2SecurityFilterChain(HttpSecurity http) throws Exception {

        JwtAuthenticationConverter customJwtAuthenticationConverter = new JwtAuthenticationConverter();
        customJwtAuthenticationConverter
                .setJwtGrantedAuthoritiesConverter(new CustomJwtGrantedAuthoritiesConverter());

        http.authorizeHttpRequests(request -> request
                        .requestMatchers( "/actuator/**", "/auth/")
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

