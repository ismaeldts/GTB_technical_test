package com.btgtechnicaltest.demo.suscripcion.domain.ports;

import com.btgtechnicaltest.demo.suscripcion.domain.model.Suscripcion;

import java.util.List;
import java.util.Optional;

public interface FindByClienteIdSuscripcionRepository {
    List<Suscripcion> findByClienteId(Long clienteId);

    Optional<Suscripcion> findByClienteIdAndFondoId(Long clienteId, Long fondoId);
}

