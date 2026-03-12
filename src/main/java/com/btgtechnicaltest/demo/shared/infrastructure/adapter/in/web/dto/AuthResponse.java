package com.btgtechnicaltest.demo.shared.infrastructure.adapter.in.web.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String email;
    private String nombre;
    private String role;
}

