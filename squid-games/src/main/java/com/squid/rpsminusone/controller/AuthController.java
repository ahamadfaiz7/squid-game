package com.squid.rpsminusone.controller;

import com.squid.rpsminusone.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Map<String, String> requestBody) {
        String username = requestBody.get("username");
        String password = requestBody.get("password");

        if (username == null || password == null) {
            return ResponseEntity.badRequest().body("Username and password must be provided");
        }

        return ResponseEntity.ok(authService.register(username, password));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        if (username == null || password == null) {
            return ResponseEntity.badRequest().body("Username and password must be provided");
        }

        try {
            String token = authService.login(username, password);
            return ResponseEntity.ok(Map.of("token", token));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }
}
