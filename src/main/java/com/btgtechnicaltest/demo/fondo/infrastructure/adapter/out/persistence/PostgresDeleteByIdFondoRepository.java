package com.btgtechnicaltest.demo.fondo.infrastructure.adapter.out.persistence;

import com.btgtechnicaltest.demo.fondo.domain.ports.DeleteByIdFondoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostgresDeleteByIdFondoRepository implements DeleteByIdFondoRepository {

    private final JpaFondoRepository jpaFondoRepository;

    @Override
    public void deleteById(Long id) {
        jpaFondoRepository.deleteById(id);
    }
}

