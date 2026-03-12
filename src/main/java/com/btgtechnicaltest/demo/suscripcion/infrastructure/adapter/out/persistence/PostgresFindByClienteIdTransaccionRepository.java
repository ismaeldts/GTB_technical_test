package com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.out.persistence;

import com.btgtechnicaltest.demo.suscripcion.domain.model.Transaccion;
import com.btgtechnicaltest.demo.suscripcion.domain.ports.FindByClienteIdTransaccionRepository;
import com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.out.persistence.mapper.TransaccionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostgresFindByClienteIdTransaccionRepository implements FindByClienteIdTransaccionRepository {

    private final JpaTransaccionRepository jpaTransaccionRepository;

    @Override
    public List<Transaccion> findByClienteId(Long clienteId) {
        return jpaTransaccionRepository.findByClienteIdOrderByFechaDesc(clienteId).stream()
                .map(TransaccionMapper::toDomain)
                .toList();
    }
}

