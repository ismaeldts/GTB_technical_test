package com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.out.persistence;

import com.btgtechnicaltest.demo.suscripcion.domain.model.Suscripcion;
import com.btgtechnicaltest.demo.suscripcion.domain.ports.FindAllSuscripcionRepository;
import com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.out.persistence.mapper.SuscripcionMapper;
import com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.out.persistence.repository.JpaSuscripcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostgresFindAllSuscripcionRepository implements FindAllSuscripcionRepository {

    private final JpaSuscripcionRepository jpaSuscripcionRepository;

    @Override
    public List<Suscripcion> findAll() {
        return jpaSuscripcionRepository.findByActivoTrue().stream()
                .map(SuscripcionMapper::toDomain)
                .toList();
    }
}

