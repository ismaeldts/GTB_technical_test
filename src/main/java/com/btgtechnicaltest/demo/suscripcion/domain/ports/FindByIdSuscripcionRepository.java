package com.btgtechnicaltest.demo.suscripcion.domain.ports;

import com.btgtechnicaltest.demo.suscripcion.domain.model.Suscripcion;

import java.util.Optional;

public interface FindByIdSuscripcionRepository {
    Optional<Suscripcion> findById(Long id);
}

