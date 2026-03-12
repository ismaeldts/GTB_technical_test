package com.btgtechnicaltest.demo.fondo.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fondos")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FondoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(name = "monto_minimo", nullable = false)
    private Double montoMinimo;

    @Column(nullable = false, length = 10)
    private String moneda;

    @Column(nullable = false, length = 100)
    private String categoria;
}
