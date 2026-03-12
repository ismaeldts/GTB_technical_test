package com.btgtechnicaltest.demo.cliente.domain.ports;

import com.btgtechnicaltest.demo.cliente.domain.model.Cliente;

public interface SaveClienteRepository {
    Cliente save(Cliente cliente);
}

