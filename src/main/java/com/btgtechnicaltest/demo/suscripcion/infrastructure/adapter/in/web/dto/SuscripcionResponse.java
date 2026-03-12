package com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.in.web.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuscripcionResponse {
    private Long id;
    private Long clienteId;
    private Long fondoId;
    private String fondoNombre;
    private String fondoCategoria;
    private Double monto;
}

