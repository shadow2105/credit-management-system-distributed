package com.example.cmscreditstatementservice.configuration;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CustomJwtGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    // Scopes signify what the client application is allowed to access on the user’s (resource owner's) behalf
    // Authorities (or roles) are typically used within an application to determine what a user can do. They are not -
    // directly related to OAuth2, but rather are part of the application’s internal authorization mechanism.

    // By default, JwtGrantedAuthoritiesConverter maps the 'scope' or 'scp' claim Strings to GrantedAuthority objects
    // see {@link org.springframework.security.oauth2.server.resource.authentication JwtAuthenticationConverter}
    // see {@link org.springframework.security.oauth2.server.resource.authentication JwtGrantedAuthoritiesConverter}
    // Maps or converts custom 'authorities' claim's Strings to GrantedAuthority objects
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {

        @SuppressWarnings("unchecked")
        List<String> authorities = (ArrayList<String>) jwt.getClaims().get("authorities");
        if (authorities == null || authorities.isEmpty()) {
            return new ArrayList<>();
        }

        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
