package com.btgtechnicaltest.demo.shared.infrastructure.adapter.in.web;

import com.btgtechnicaltest.demo.shared.application.AuthService;
import com.btgtechnicaltest.demo.shared.infrastructure.adapter.in.web.dto.AuthResponse;
import com.btgtechnicaltest.demo.shared.infrastructure.adapter.in.web.dto.LoginRequest;
import com.btgtechnicaltest.demo.shared.infrastructure.adapter.in.web.dto.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints de registro y login de clientes")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Registrar cliente", description = "Crea un nuevo cliente y retorna un token JWT")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @Operation(summary = "Iniciar sesión", description = "Autentica un cliente y retorna un token JWT")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}

