package com.btgtechnicaltest.demo.cliente.domain.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
    private Long id;
    private String nombre;
    private String email;
    private String apellidos;
    private String ciudad;
    private String password;
    private String role;
    private Double saldo;
}
