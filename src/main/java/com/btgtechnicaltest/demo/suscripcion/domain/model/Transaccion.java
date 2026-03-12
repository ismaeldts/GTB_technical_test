package com.btgtechnicaltest.demo.suscripcion.domain.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaccion {
    private UUID id;
    private Long clienteId;
    private Long fondoId;
    private String tipo;
    private Double monto;
    private LocalDateTime fecha;

    private String fondoNombre;
    private String fondoCategoria;

    public static Transaccion apertura(Long clienteId, FondoInfo fondo) {
        return Transaccion.builder()
                .id(UUID.randomUUID())
                .clienteId(clienteId)
                .fondoId(fondo.getId())
                .tipo("apertura")
                .monto(fondo.getMontoMinimo())
                .fecha(LocalDateTime.now())
                .fondoNombre(fondo.getNombre())
                .fondoCategoria(fondo.getCategoria())
                .build();
    }

    public static Transaccion cancelacion(Long clienteId, FondoInfo fondo, Double monto) {
        return Transaccion.builder()
                .id(UUID.randomUUID())
                .clienteId(clienteId)
                .fondoId(fondo.getId())
                .tipo("cancelacion")
                .monto(monto)
                .fecha(LocalDateTime.now())
                .fondoNombre(fondo.getNombre())
                .fondoCategoria(fondo.getCategoria())
                .build();
    }
}

