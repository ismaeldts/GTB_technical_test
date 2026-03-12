package com.btgtechnicaltest.demo.suscripcion.domain.ports;

import com.btgtechnicaltest.demo.suscripcion.domain.model.FondoInfo;

import java.util.Optional;

public interface FondoExternalPort {
    Optional<FondoInfo> findById(Long fondoId);
}

