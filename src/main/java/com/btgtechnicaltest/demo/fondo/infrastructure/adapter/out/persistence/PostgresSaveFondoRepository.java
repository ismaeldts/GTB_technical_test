package com.btgtechnicaltest.demo.fondo.infrastructure.adapter.out.persistence;

import com.btgtechnicaltest.demo.fondo.domain.model.Fondo;
import com.btgtechnicaltest.demo.fondo.domain.ports.SaveFondoRepository;
import com.btgtechnicaltest.demo.fondo.infrastructure.adapter.out.persistence.mapper.FondoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostgresSaveFondoRepository implements SaveFondoRepository {

    private final JpaFondoRepository jpaFondoRepository;

    @Override
    public Fondo save(Fondo fondo) {
        var entity = FondoMapper.toEntity(fondo);
        var saved = jpaFondoRepository.save(entity);
        return FondoMapper.toDomain(saved);
    }
}

