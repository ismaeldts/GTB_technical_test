package com.btgtechnicaltest.demo.cliente.infrastructure.adapter.in.web.mapper;

import com.btgtechnicaltest.demo.cliente.domain.model.Cliente;
import com.btgtechnicaltest.demo.cliente.infrastructure.adapter.in.web.dto.ClienteResponse;

public class ClienteResponseMapper {

    private ClienteResponseMapper() {
    }

    public static ClienteResponse fromDomain(
            Cliente cliente) {
        return ClienteResponse.builder()
                .id(cliente.getId())
                .nombre(cliente.getNombre())
                .apellidos(cliente.getApellidos())
                .ciudad(cliente.getCiudad())
                .email(cliente.getEmail())
                .build();
    }
}
