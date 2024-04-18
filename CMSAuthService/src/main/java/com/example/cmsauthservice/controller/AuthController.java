package com.example.cmsauthservice.controller;

import com.example.cmsauthservice.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Value("${com.public-client-uri}")
    private String publicClientUri;
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /*@RequestMapping("/")
    public ResponseEntity<String> index() {
        return ResponseEntity.ok("Auth Service is running");
    }*/

    @RequestMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyAppAuthorization(HttpServletResponse response)
            throws IOException
    {
        // System.out.println("\n>>>>>>>>> I was called!");

        if (!authService.hasAppAuthorization()) {
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("error", "Forbidden");
            responseBody.put("message", """
            You are not authorized to use the application.
            Contact Support to register for the application.
            """
            );

            RestTemplate restTemplate = new RestTemplate();

            // GET issuer from access token
            // "iss": "http://localhost:8090"
            String url = "http://localhost:8090/logout/back-channel";

            // Make the POST request with the client id
            restTemplate.postForEntity(url, null, String.class);

            //response.sendRedirect(url);

            return ResponseEntity.status(403).body(responseBody);
        }

        response.sendRedirect(publicClientUri);
        return null;
    }

}


