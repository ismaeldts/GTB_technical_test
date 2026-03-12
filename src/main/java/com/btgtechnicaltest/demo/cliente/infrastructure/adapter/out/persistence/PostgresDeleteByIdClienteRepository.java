package com.btgtechnicaltest.demo.cliente.infrastructure.adapter.out.persistence;

import com.btgtechnicaltest.demo.cliente.domain.ports.DeleteByIdClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostgresDeleteByIdClienteRepository implements DeleteByIdClienteRepository {

    private final JpaClienteRepository jpaClienteRepository;

    @Override
    public void deleteById(Long id) {
        jpaClienteRepository.deleteById(id);
    }
}

