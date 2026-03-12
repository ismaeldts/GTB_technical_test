package com.btgtechnicaltest.demo.cliente.infrastructure.adapter.out.persistence.mapper;

import com.btgtechnicaltest.demo.cliente.domain.model.Cliente;
import com.btgtechnicaltest.demo.cliente.infrastructure.adapter.out.persistence.entity.ClienteEntity;

public class ClienteMapper {

    private ClienteMapper() {
    }

    public static Cliente toDomain(ClienteEntity entity) {
        return Cliente.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .email(entity.getEmail())
                .apellidos(entity.getApellidos())
                .ciudad(entity.getCiudad())
                .password(entity.getPassword())
                .role(entity.getRole())
                .saldo(entity.getSaldo())
                .build();
    }

    public static ClienteEntity toEntity(Cliente cliente) {
        return ClienteEntity.builder()
                .id(cliente.getId())
                .nombre(cliente.getNombre())
                .email(cliente.getEmail())
                .apellidos(cliente.getApellidos())
                .ciudad(cliente.getCiudad())
                .password(cliente.getPassword())
                .role(cliente.getRole())
                .saldo(cliente.getSaldo())
                .build();
    }
}

