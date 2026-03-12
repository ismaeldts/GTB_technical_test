package com.btgtechnicaltest.demo.fondo.infrastructure.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FondoRequest {
    private String nombre;
    private Double montoMinimo;
    private String moneda;
    private String categoria;
}

