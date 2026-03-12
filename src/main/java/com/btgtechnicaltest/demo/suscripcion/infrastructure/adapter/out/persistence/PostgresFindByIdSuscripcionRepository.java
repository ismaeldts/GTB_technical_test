package com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.out.persistence;

import com.btgtechnicaltest.demo.suscripcion.domain.model.Suscripcion;
import com.btgtechnicaltest.demo.suscripcion.domain.ports.FindByIdSuscripcionRepository;
import com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.out.persistence.mapper.SuscripcionMapper;
import com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.out.persistence.repository.JpaSuscripcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostgresFindByIdSuscripcionRepository implements FindByIdSuscripcionRepository {

    private final JpaSuscripcionRepository jpaSuscripcionRepository;

    @Override
    public Optional<Suscripcion> findById(Long id) {
        return jpaSuscripcionRepository.findByIdAndActivoTrue(id).map(SuscripcionMapper::toDomain);
    }
}

