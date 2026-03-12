package com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.out.persistence.mapper;

import com.btgtechnicaltest.demo.suscripcion.domain.model.Transaccion;
import com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.out.persistence.entity.TransaccionEntity;

public class TransaccionMapper {

    private TransaccionMapper() {
    }

    public static Transaccion toDomain(TransaccionEntity entity) {
        return Transaccion.builder()
                .id(entity.getId())
                .clienteId(entity.getClienteId())
                .fondoId(entity.getFondoId())
                .tipo(entity.getTipo())
                .monto(entity.getMonto())
                .fecha(entity.getFecha())
                .build();
    }

    public static TransaccionEntity toEntity(Transaccion transaccion) {
        return TransaccionEntity.builder()
                .id(transaccion.getId())
                .clienteId(transaccion.getClienteId())
                .fondoId(transaccion.getFondoId())
                .tipo(transaccion.getTipo())
                .monto(transaccion.getMonto())
                .fecha(transaccion.getFecha())
                .build();
    }
}

