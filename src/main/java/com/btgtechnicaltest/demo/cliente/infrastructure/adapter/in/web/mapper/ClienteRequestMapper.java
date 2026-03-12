package com.btgtechnicaltest.demo.cliente.infrastructure.adapter.in.web.mapper;

import com.btgtechnicaltest.demo.cliente.domain.model.Cliente;
import com.btgtechnicaltest.demo.cliente.infrastructure.adapter.in.web.dto.ClienteRequest;

public class ClienteRequestMapper {

    private ClienteRequestMapper() {
    }

    public static Cliente toDomain(ClienteRequest request) {
        return Cliente.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .apellidos(request.getApellidos())
                .ciudad(request.getCiudad())
                .build();
    }

}
