package com.btgtechnicaltest.demo.suscripcion.domain.ports;

import com.btgtechnicaltest.demo.suscripcion.domain.model.ClienteInfo;

import java.util.Optional;

public interface ClienteExternalPort {
    boolean existsById(Long clienteId);

    Optional<ClienteInfo> findByEmail(String email);

    void debitarSaldo(Long clienteId, Double monto);

    void acreditarSaldo(Long clienteId, Double monto);
}

