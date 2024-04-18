package com.example.cmsapigateway.configuration;

import com.example.cmsapigateway.controller.CustomLoginSuccessHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestCustomizers;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.net.URI;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration {

    @Value("${com.public-client-uri}")
    private String publicClientUri;

    @Value("${com.issuer-uri}")
    private String issuerUri;

    private final ClientRegistrationRepository clientRegistrationRepository;

    private final CustomLoginSuccessHandler customLoginSuccessHandler;

    public SecurityConfiguration(ClientRegistrationRepository clientRegistrationRepository, CustomLoginSuccessHandler customLoginSuccessHandler) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.customLoginSuccessHandler = customLoginSuccessHandler;
    }

    @Bean
    // For API Testing with Postman (This application only works as an API Gateway)
    @Profile("default")
    public SecurityFilterChain oauth2SecurityFilterChainDefault(HttpSecurity http,
                                                         ClientRegistrationRepository repo) throws Exception {

        String baseUri = OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI;
        DefaultOAuth2AuthorizationRequestResolver resolver = new DefaultOAuth2AuthorizationRequestResolver(repo, baseUri);

        resolver.setAuthorizationRequestCustomizer(OAuth2AuthorizationRequestCustomizers.withPkce());

        http.authorizeHttpRequests(request -> request
                        .requestMatchers( "/resources/**", "/actuator/**", "/api/v1/**")
                        .permitAll()
                        .anyRequest().authenticated())
                        .oauth2Login((login) -> login
                            .authorizationEndpoint(authorizationEndpointConfig -> authorizationEndpointConfig
                                    //.baseUri("/oauth2/authorization")
                                    .authorizationRequestResolver(resolver)
                            )
                        )
                        .csrf((AbstractHttpConfigurer::disable))
                        .cors(Customizer.withDefaults());   // Enable CORS

        return http.build();
    }

    @Bean
    // Web Application (This application works as an API Gateway and serves as a Backend of the Client - BFF Pattern)
    @Profile({"dev", "prod"})
    public SecurityFilterChain oauth2SecurityFilterChain(HttpSecurity http,
                                                         ClientRegistrationRepository repo) throws Exception {

        String baseUri = OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI;
        DefaultOAuth2AuthorizationRequestResolver resolver = new DefaultOAuth2AuthorizationRequestResolver(repo, baseUri);

        resolver.setAuthorizationRequestCustomizer(OAuth2AuthorizationRequestCustomizers.withPkce());

        http.authorizeHttpRequests(request -> request
                        .requestMatchers( "/resources/**", "/actuator/**", "/authn/**")
                        .permitAll()
                        .anyRequest().authenticated())
                        .oauth2Login((login) -> login
                                        .authorizationEndpoint(authorizationEndpointConfig -> authorizationEndpointConfig
                                                //.baseUri("/oauth2/authorization")
                                                .authorizationRequestResolver(resolver)
                                        )
                                        //.defaultSuccessUrl(publicClientUri, true)
                                        .successHandler(customLoginSuccessHandler)
                        )
                        .logout((logout) -> logout
                                        .logoutSuccessHandler(oidcLogoutSuccessHandler())
                        )
                        // Securing POST /logout/connect/back-channel/cms
                        // "2024-03-21T12:08:49.594-04:00 DEBUG 15156 --- [cms-api-gateway] [nio-8080-exec-4]
                        // .c.o.c.OidcLogoutAuthenticationConverter : Failed to process OIDC Back-Channel Logout since
                        // no logout token was found"

                        // Spring Authorization Server might not support back-channel logout
                        // No 'backchannel_logout_support' metadata value in OIDC Provider metadata, implying false
                        // Might need to manually create a logout token
                        // https://openid.net/specs/openid-connect-backchannel-1_0.html#LogoutToken
                        .oidcLogout((logout) -> logout
                                        .backChannel(Customizer.withDefaults())
                        )
                        //.csrf((AbstractHttpConfigurer::disable))
                        .cors(Customizer.withDefaults());   // Enable CORS

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.addAllowedOrigin(publicClientUri);
        config.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", config);
        return source;
    }


    /*
     * Registering an OAuth2 Client by the following way is not recommended (Use application properties instead)
        - no way to specify OpenID Connect Discovery Endpoint (used by clients to discover OIDC Provider metadata)
          /.well-known/openid-configuration

        - no direct way to specify OIDC Logout Endpoint

        - only specifying the issuer URI isn't sufficient, since OIDC Discovery Endpoint is never requested
          Following endpoints need to explicitly configured -
            - Authorization URI
            - Token URI
            - JwkSet URI

        - providerConfigurationMetadata - a hashmap to explicitly specify OIDC Provider metadata

     * The application will not start before the authorization server (Issuer URI) if using application properties
     * to register an OAuth2 Client

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(this.getClientRegistration());
    }

    private ClientRegistration getClientRegistration() {
        return ClientRegistration.withRegistrationId("cms")
                .clientId("9DFD919F17AD2C97C24E543C3F954DD3")
                //.clientSecret("6JyvSMHFqjKL9Pvo47irtLrKTC17yn7yLyqHh6hB3uQ=")
                //.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .scope("openid", "profile", "email", "address")
                .issuerUri(issuerUri)
                .authorizationUri(issuerUri + "/oauth2/authorize")
                .tokenUri(issuerUri + "/oauth2/token")
                .jwkSetUri(issuerUri + "/oauth2/jwks")
                //.providerConfigurationMetadata()
                .build();
    }
    */

    private OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler() {
        OidcClientInitiatedLogoutSuccessHandler successHandler =
                new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);

        successHandler.setPostLogoutRedirectUri(String.valueOf(URI.create(publicClientUri)));
        return successHandler;
    }
}
