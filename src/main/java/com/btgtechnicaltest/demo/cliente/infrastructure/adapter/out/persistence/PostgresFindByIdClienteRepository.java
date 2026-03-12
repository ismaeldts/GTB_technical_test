package com.btgtechnicaltest.demo.cliente.infrastructure.adapter.out.persistence;

import com.btgtechnicaltest.demo.cliente.domain.model.Cliente;
import com.btgtechnicaltest.demo.cliente.domain.ports.FindByIdClienteRepository;
import com.btgtechnicaltest.demo.cliente.infrastructure.adapter.out.persistence.mapper.ClienteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostgresFindByIdClienteRepository implements FindByIdClienteRepository {

    private final JpaClienteRepository jpaClienteRepository;

    @Override
    public Optional<Cliente> findById(Long id) {
        return jpaClienteRepository.findById(id).map(ClienteMapper::toDomain);
    }
}

