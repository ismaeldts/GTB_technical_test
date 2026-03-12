package com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.out.persistence;

import com.btgtechnicaltest.demo.suscripcion.domain.ports.DeleteByIdSuscripcionRepository;
import com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.out.persistence.entity.SuscripcionEntity;
import com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.out.persistence.repository.JpaSuscripcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostgresDeleteByIdSuscripcionRepository implements DeleteByIdSuscripcionRepository {

    private final JpaSuscripcionRepository jpaSuscripcionRepository;

    @Override
    public void deleteById(Long id) {
        SuscripcionEntity entity = jpaSuscripcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Suscripción no encontrada con id: " + id));
        entity.setActivo(false);
        jpaSuscripcionRepository.save(entity);
    }
}

