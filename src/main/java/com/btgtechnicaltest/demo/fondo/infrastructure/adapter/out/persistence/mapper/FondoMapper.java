package com.btgtechnicaltest.demo.fondo.infrastructure.adapter.out.persistence.mapper;

import com.btgtechnicaltest.demo.fondo.domain.model.Fondo;
import com.btgtechnicaltest.demo.fondo.domain.model.Moneda;
import com.btgtechnicaltest.demo.fondo.infrastructure.adapter.out.persistence.entity.FondoEntity;

public class FondoMapper {

    private FondoMapper() {
    }

    public static Fondo toDomain(FondoEntity entity) {
        return Fondo.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .montoMinimo(entity.getMontoMinimo())
                .moneda(Moneda.valueOf(entity.getMoneda()))
                .categoria(entity.getCategoria())
                .build();
    }

    public static FondoEntity toEntity(Fondo fondo) {
        return FondoEntity.builder()
                .id(fondo.getId())
                .nombre(fondo.getNombre())
                .montoMinimo(fondo.getMontoMinimo())
                .moneda(fondo.getMoneda().name())
                .categoria(fondo.getCategoria())
                .build();
    }
}

