package com.example.cmsapigateway.controller;

import com.example.cmsapigateway.service.AuthnService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/authn")
public class AuthnController {

    @Value("${com.issuer-uri}")
    private String issuerUri;
    private final AuthnService authnService;

    public AuthnController(AuthnService authnService) {
        this.authnService = authnService;
    }

    @GetMapping("/")
    public ResponseEntity<String> index() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        System.out.println(authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return ResponseEntity.ok("Authentication Service is running!");
    }

    @RequestMapping("/signup")
    public void handleSignup(HttpServletResponse response) throws IOException {
        response.sendRedirect(issuerUri + "/signin/register/9DFD919F17AD2C97C24E543C3F954DD3");
    }

    @RequestMapping("/status")
    public ResponseEntity<Map<String, Object>> sendAuthenticationStatus() throws IOException {
        //System.out.println(authnService.getAuthnStatus());
        return ResponseEntity.ok().body(authnService.getAuthnStatus());
    }
}
