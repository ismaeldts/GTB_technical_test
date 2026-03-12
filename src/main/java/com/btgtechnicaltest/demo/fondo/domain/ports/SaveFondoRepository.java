package com.btgtechnicaltest.demo.fondo.domain.ports;

import com.btgtechnicaltest.demo.fondo.domain.model.Fondo;

public interface SaveFondoRepository {
    Fondo save(Fondo fondo);
}

