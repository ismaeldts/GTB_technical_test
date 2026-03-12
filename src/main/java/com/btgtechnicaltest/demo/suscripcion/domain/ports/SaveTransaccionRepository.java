package com.btgtechnicaltest.demo.suscripcion.domain.ports;

import com.btgtechnicaltest.demo.suscripcion.domain.model.Transaccion;

public interface SaveTransaccionRepository {
    Transaccion save(Transaccion transaccion);
}

