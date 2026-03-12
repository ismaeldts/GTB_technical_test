package com.btgtechnicaltest.demo.shared.application;

import com.btgtechnicaltest.demo.cliente.application.ClienteService;
import com.btgtechnicaltest.demo.cliente.domain.model.Cliente;
import com.btgtechnicaltest.demo.shared.infrastructure.adapter.in.web.dto.AuthResponse;
import com.btgtechnicaltest.demo.shared.infrastructure.adapter.in.web.dto.LoginRequest;
import com.btgtechnicaltest.demo.shared.infrastructure.adapter.in.web.dto.RegisterRequest;
import com.btgtechnicaltest.demo.shared.infrastructure.config.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ClienteService clienteService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public AuthResponse register(RegisterRequest request) {
        Cliente cliente = Cliente.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .apellidos(request.getApellidos())
                .ciudad(request.getCiudad())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("ROLE_CLIENTE")
                .saldo(500000.0)
                .build();

        Cliente saved = clienteService.crear(cliente);

        UserDetails userDetails = userDetailsService.loadUserByUsername(saved.getEmail());
        String token = jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .email(saved.getEmail())
                .nombre(saved.getNombre())
                .role(saved.getRole())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .email(request.getEmail())
                .nombre(userDetails.getUsername())
                .role(userDetails.getAuthorities().iterator().next().getAuthority())
                .build();
    }
}

