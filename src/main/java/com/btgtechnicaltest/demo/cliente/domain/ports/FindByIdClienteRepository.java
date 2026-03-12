package com.btgtechnicaltest.demo.cliente.domain.ports;

import com.btgtechnicaltest.demo.cliente.domain.model.Cliente;

import java.util.Optional;

public interface FindByIdClienteRepository {
    Optional<Cliente> findById(Long id);
}

