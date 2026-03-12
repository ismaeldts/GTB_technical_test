package com.btgtechnicaltest.demo.suscripcion.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ClienteInfo {
    private final Long id;
    private final String email;
    private final Double saldo;
}

