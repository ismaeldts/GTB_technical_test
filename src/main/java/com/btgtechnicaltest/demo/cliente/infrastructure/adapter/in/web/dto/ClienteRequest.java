package com.btgtechnicaltest.demo.cliente.infrastructure.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClienteRequest {
    private String nombre;
    private String email;
    private String apellidos;
    private String ciudad;
}

