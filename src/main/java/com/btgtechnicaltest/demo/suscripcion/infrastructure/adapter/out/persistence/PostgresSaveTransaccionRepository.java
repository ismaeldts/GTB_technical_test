package com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.out.persistence;

import com.btgtechnicaltest.demo.suscripcion.domain.model.Transaccion;
import com.btgtechnicaltest.demo.suscripcion.domain.ports.SaveTransaccionRepository;
import com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.out.persistence.entity.TransaccionEntity;
import com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.out.persistence.mapper.TransaccionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class PostgresSaveTransaccionRepository implements SaveTransaccionRepository {

    private final JpaTransaccionRepository jpaTransaccionRepository;

    @Override
    @Transactional
    public Transaccion save(Transaccion transaccion) {
        TransaccionEntity entity = TransaccionMapper.toEntity(transaccion);
        return TransaccionMapper.toDomain(jpaTransaccionRepository.save(entity));
    }
}

