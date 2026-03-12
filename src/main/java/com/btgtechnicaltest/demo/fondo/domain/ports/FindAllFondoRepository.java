package com.btgtechnicaltest.demo.fondo.domain.ports;

import com.btgtechnicaltest.demo.fondo.domain.model.Fondo;

import java.util.List;

public interface FindAllFondoRepository {
    List<Fondo> findAll();
}

