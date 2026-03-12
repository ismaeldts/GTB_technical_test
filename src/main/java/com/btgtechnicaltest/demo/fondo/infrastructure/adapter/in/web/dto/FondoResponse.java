package com.btgtechnicaltest.demo.fondo.infrastructure.adapter.in.web.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FondoResponse {
    private Long id;
    private String nombre;
    private Double montoMinimo;
    private String moneda;
    private String categoria;
}

