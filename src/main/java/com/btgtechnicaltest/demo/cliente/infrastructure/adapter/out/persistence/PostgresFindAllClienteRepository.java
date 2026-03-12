package com.btgtechnicaltest.demo.cliente.infrastructure.adapter.out.persistence;

import com.btgtechnicaltest.demo.cliente.domain.model.Cliente;
import com.btgtechnicaltest.demo.cliente.domain.ports.FindAllClienteRepository;
import com.btgtechnicaltest.demo.cliente.infrastructure.adapter.out.persistence.mapper.ClienteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostgresFindAllClienteRepository implements FindAllClienteRepository {

    private final JpaClienteRepository jpaClienteRepository;

    @Override
    public List<Cliente> findAll() {
        return jpaClienteRepository.findAll().stream()
                .map(ClienteMapper::toDomain)
                .toList();
    }
}

