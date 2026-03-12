package com.btgtechnicaltest.demo.suscripcion.domain.ports;

import com.btgtechnicaltest.demo.suscripcion.domain.model.Suscripcion;

import java.util.List;

public interface FindAllSuscripcionRepository {
    List<Suscripcion> findAll();
}

