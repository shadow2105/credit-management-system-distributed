package com.example.cmsapigateway.service;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

// Authentication Service
@Service
public class AuthnService {

    public Map<String, Object> getAuthnStatus() {

        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        Map<String, Object> statusResponse = new HashMap<>();

        // https://docs.spring.io/spring-security/site/docs/4.0.x/reference/html/anonymous.html
        if (authentication instanceof AnonymousAuthenticationToken) {
            statusResponse.put("isAuthenticated", false);
            statusResponse.put("userData", null);
        }
        else {
            OidcIdToken idToken = ((DefaultOidcUser) authentication.getPrincipal()).getIdToken();

            Map<String, Object> userData = new HashMap<>();
            userData.put(StandardClaimNames.SUB, idToken.getClaim(StandardClaimNames.SUB));
            userData.put(StandardClaimNames.GIVEN_NAME, idToken.getClaim(StandardClaimNames.GIVEN_NAME));
            userData.put(StandardClaimNames.MIDDLE_NAME, idToken.getClaim(StandardClaimNames.MIDDLE_NAME));
            userData.put(StandardClaimNames.FAMILY_NAME, idToken.getClaim(StandardClaimNames.FAMILY_NAME));
            userData.put(StandardClaimNames.PREFERRED_USERNAME, idToken.getClaim(StandardClaimNames.PREFERRED_USERNAME));
            userData.put(StandardClaimNames.PICTURE, idToken.getClaim(StandardClaimNames.PICTURE));
            userData.put(StandardClaimNames.LOCALE, idToken.getClaim(StandardClaimNames.LOCALE));
            userData.put(StandardClaimNames.ZONEINFO, idToken.getClaim(StandardClaimNames.ZONEINFO));
            userData.put(StandardClaimNames.EMAIL, idToken.getClaim(StandardClaimNames.EMAIL));
            userData.put(StandardClaimNames.EMAIL_VERIFIED, idToken.getClaim(StandardClaimNames.EMAIL_VERIFIED));
            userData.put("is_profile_complete", idToken.getClaim("is_profile_complete"));

            statusResponse.put("isAuthenticated", true);
            statusResponse.put("userData", userData);
        }

        return statusResponse;
    }
}
