package com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.out.persistence.mapper;

import com.btgtechnicaltest.demo.suscripcion.domain.model.Suscripcion;
import com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.out.persistence.entity.SuscripcionEntity;

public class SuscripcionMapper {

    private SuscripcionMapper() {
    }

    public static Suscripcion toDomain(SuscripcionEntity entity) {
        return Suscripcion.builder()
                .id(entity.getId())
                .clienteId(entity.getClienteId())
                .fondoId(entity.getFondoId())
                .monto(entity.getMonto())
                .activo(entity.getActivo())
                .build();
    }

    public static SuscripcionEntity toEntity(Suscripcion suscripcion) {
        return SuscripcionEntity.builder()
                .id(suscripcion.getId())
                .clienteId(suscripcion.getClienteId())
                .fondoId(suscripcion.getFondoId())
                .monto(suscripcion.getMonto())
                .activo(suscripcion.getActivo())
                .build();
    }
}

