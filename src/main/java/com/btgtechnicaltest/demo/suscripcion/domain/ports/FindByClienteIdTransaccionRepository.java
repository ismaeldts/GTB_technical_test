package com.btgtechnicaltest.demo.suscripcion.domain.ports;

import com.btgtechnicaltest.demo.suscripcion.domain.model.Transaccion;

import java.util.List;

public interface FindByClienteIdTransaccionRepository {
    List<Transaccion> findByClienteId(Long clienteId);
}

