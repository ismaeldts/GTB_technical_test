package com.btgtechnicaltest.demo.fondo.domain.ports;

import com.btgtechnicaltest.demo.fondo.domain.model.Fondo;

import java.util.Optional;

public interface FindByNombreFondoRepository {
    Optional<Fondo> findByNombre(String nombre);
}

