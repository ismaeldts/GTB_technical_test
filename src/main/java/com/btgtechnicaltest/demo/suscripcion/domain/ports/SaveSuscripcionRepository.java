package com.btgtechnicaltest.demo.suscripcion.domain.ports;

import com.btgtechnicaltest.demo.suscripcion.domain.model.Suscripcion;

public interface SaveSuscripcionRepository {
    Suscripcion save(Suscripcion suscripcion);
}

