package com.btgtechnicaltest.demo.fondo.domain.ports;

import com.btgtechnicaltest.demo.fondo.domain.model.Fondo;

import java.util.Optional;

public interface FindByIdFondoRepository {
    Optional<Fondo> findById(Long id);
}

