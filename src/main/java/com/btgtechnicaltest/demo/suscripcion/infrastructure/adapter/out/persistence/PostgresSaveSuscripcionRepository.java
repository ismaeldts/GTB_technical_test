package com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.out.persistence;

import com.btgtechnicaltest.demo.suscripcion.domain.model.Suscripcion;
import com.btgtechnicaltest.demo.suscripcion.domain.ports.SaveSuscripcionRepository;
import com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.out.persistence.mapper.SuscripcionMapper;
import com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.out.persistence.repository.JpaSuscripcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class PostgresSaveSuscripcionRepository implements SaveSuscripcionRepository {

    private final JpaSuscripcionRepository jpaSuscripcionRepository;

    @Override
    @Transactional
    public Suscripcion save(Suscripcion suscripcion) {
        var entity = SuscripcionMapper.toEntity(suscripcion);
        return SuscripcionMapper.toDomain(jpaSuscripcionRepository.save(entity));
    }
}

