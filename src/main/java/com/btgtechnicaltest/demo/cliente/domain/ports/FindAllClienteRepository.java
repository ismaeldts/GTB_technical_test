package com.btgtechnicaltest.demo.cliente.domain.ports;

import com.btgtechnicaltest.demo.cliente.domain.model.Cliente;

import java.util.List;

public interface FindAllClienteRepository {
    List<Cliente> findAll();
}

