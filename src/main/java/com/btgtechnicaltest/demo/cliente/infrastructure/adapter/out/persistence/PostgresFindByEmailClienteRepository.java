package com.btgtechnicaltest.demo.cliente.infrastructure.adapter.out.persistence;

import com.btgtechnicaltest.demo.cliente.domain.model.Cliente;
import com.btgtechnicaltest.demo.cliente.domain.ports.FindByEmailClienteRepository;
import com.btgtechnicaltest.demo.cliente.infrastructure.adapter.out.persistence.mapper.ClienteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostgresFindByEmailClienteRepository implements FindByEmailClienteRepository {

    private final JpaClienteRepository jpaClienteRepository;

    @Override
    public Optional<Cliente> findByEmail(String email) {
        return jpaClienteRepository.findByEmail(email).map(ClienteMapper::toDomain);
    }
}

