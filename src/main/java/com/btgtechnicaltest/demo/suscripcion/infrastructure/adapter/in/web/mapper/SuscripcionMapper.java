package com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.in.web.mapper;

import com.btgtechnicaltest.demo.suscripcion.domain.model.Suscripcion;
import com.btgtechnicaltest.demo.suscripcion.domain.model.Transaccion;
import com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.in.web.dto.SuscripcionResponse;
import com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.in.web.dto.TransaccionResponse;
import org.springframework.stereotype.Component;

@Component
public class SuscripcionMapper {

    public SuscripcionResponse toResponse(Suscripcion suscripcion) {
        return SuscripcionResponse.builder()
                .id(suscripcion.getId())
                .clienteId(suscripcion.getClienteId())
                .fondoId(suscripcion.getFondoId())
                .fondoNombre(suscripcion.getFondoNombre())
                .fondoCategoria(suscripcion.getFondoCategoria())
                .monto(suscripcion.getMonto())
                .build();
    }

    public TransaccionResponse toTransaccionResponse(Transaccion transaccion) {
        return TransaccionResponse.builder()
                .id(transaccion.getId())
                .fondoId(transaccion.getFondoId())
                .fondoNombre(transaccion.getFondoNombre())
                .fondoCategoria(transaccion.getFondoCategoria())
                .tipo(transaccion.getTipo())
                .monto(transaccion.getMonto())
                .fecha(transaccion.getFecha())
                .build();
    }
}

