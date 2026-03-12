package com.btgtechnicaltest.demo.cliente.infrastructure.adapter.in.web.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponse {
    private Long id;
    private String nombre;
    private String email;
    private String apellidos;
    private String ciudad;
}

