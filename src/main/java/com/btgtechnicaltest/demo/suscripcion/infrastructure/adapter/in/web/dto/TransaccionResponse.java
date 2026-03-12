package com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.in.web.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransaccionResponse {
    private UUID id;
    private Long fondoId;
    private String fondoNombre;
    private String fondoCategoria;
    private String tipo;
    private Double monto;
    private LocalDateTime fecha;
}

