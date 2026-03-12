package com.btgtechnicaltest.demo.cliente.infrastructure.adapter.in.web.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecargaSaldoResponse {
    private String message;
    private String email;
    private Double saldoActual;
}

