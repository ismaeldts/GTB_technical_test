package com.btgtechnicaltest.demo.fondo.domain.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Fondo {
    private Long id;
    private String nombre;
    private Double montoMinimo;
    private Moneda moneda;
    private String categoria;
}

