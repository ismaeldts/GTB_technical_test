package com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "suscripciones")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuscripcionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(name = "fondo_id", nullable = false)
    private Long fondoId;

    @Column(nullable = false)
    private Double monto;

    @Column(nullable = false)
    private Boolean activo;
}

