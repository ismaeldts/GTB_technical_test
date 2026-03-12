package com.btgtechnicaltest.demo.fondo.infrastructure.adapter.out.persistence;

import com.btgtechnicaltest.demo.fondo.domain.model.Fondo;
import com.btgtechnicaltest.demo.fondo.domain.ports.FindByNombreFondoRepository;
import com.btgtechnicaltest.demo.fondo.infrastructure.adapter.out.persistence.mapper.FondoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostgresFindByNombreFondoRepository implements FindByNombreFondoRepository {

    private final JpaFondoRepository jpaFondoRepository;

    @Override
    public Optional<Fondo> findByNombre(String nombre) {
        return jpaFondoRepository.findByNombre(nombre).map(FondoMapper::toDomain);
    }
}

