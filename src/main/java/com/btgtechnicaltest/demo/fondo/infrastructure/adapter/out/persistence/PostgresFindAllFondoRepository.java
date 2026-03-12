package com.btgtechnicaltest.demo.fondo.infrastructure.adapter.out.persistence;

import com.btgtechnicaltest.demo.fondo.domain.model.Fondo;
import com.btgtechnicaltest.demo.fondo.domain.ports.FindAllFondoRepository;
import com.btgtechnicaltest.demo.fondo.infrastructure.adapter.out.persistence.mapper.FondoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostgresFindAllFondoRepository implements FindAllFondoRepository {

    private final JpaFondoRepository jpaFondoRepository;

    @Override
    public List<Fondo> findAll() {
        return jpaFondoRepository.findAll().stream()
                .map(FondoMapper::toDomain)
                .toList();
    }
}

