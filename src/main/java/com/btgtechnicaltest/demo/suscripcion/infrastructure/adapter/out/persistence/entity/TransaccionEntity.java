package com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transacciones")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransaccionEntity {

    @Id
    private UUID id;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(name = "fondo_id", nullable = false)
    private Long fondoId;

    @Column(nullable = false, length = 20)
    private String tipo;

    @Column(nullable = false)
    private Double monto;

    @Column(nullable = false)
    private LocalDateTime fecha;
}

