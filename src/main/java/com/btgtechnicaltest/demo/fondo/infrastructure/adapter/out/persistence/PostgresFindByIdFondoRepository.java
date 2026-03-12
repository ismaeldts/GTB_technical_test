package com.btgtechnicaltest.demo.fondo.infrastructure.adapter.out.persistence;

import com.btgtechnicaltest.demo.fondo.domain.model.Fondo;
import com.btgtechnicaltest.demo.fondo.domain.ports.FindByIdFondoRepository;
import com.btgtechnicaltest.demo.fondo.infrastructure.adapter.out.persistence.mapper.FondoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostgresFindByIdFondoRepository implements FindByIdFondoRepository {

    private final JpaFondoRepository jpaFondoRepository;

    @Override
    public Optional<Fondo> findById(Long id) {
        return jpaFondoRepository.findById(id).map(FondoMapper::toDomain);
    }
}

