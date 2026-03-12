package com.btgtechnicaltest.demo.suscripcion.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class FondoInfo {
    private final Long id;
    private final String nombre;
    private final Double montoMinimo;
    private final String categoria;
}

