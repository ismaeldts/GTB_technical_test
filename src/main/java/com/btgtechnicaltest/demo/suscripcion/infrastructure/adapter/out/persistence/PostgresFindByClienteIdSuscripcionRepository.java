package com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.out.persistence;

import com.btgtechnicaltest.demo.suscripcion.domain.model.Suscripcion;
import com.btgtechnicaltest.demo.suscripcion.domain.ports.FindByClienteIdSuscripcionRepository;
import com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.out.persistence.mapper.SuscripcionMapper;
import com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.out.persistence.repository.JpaSuscripcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostgresFindByClienteIdSuscripcionRepository implements FindByClienteIdSuscripcionRepository {

    private final JpaSuscripcionRepository jpaSuscripcionRepository;

    @Override
    public List<Suscripcion> findByClienteId(Long clienteId) {
        return jpaSuscripcionRepository.findByClienteIdAndActivoTrue(clienteId).stream()
                .map(SuscripcionMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Suscripcion> findByClienteIdAndFondoId(Long clienteId, Long fondoId) {
        return jpaSuscripcionRepository.findByClienteIdAndFondoIdAndActivoTrue(clienteId, fondoId)
                .map(SuscripcionMapper::toDomain);
    }
}

