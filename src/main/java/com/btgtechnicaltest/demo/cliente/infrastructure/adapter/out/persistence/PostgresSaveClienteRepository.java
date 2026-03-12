package com.btgtechnicaltest.demo.cliente.infrastructure.adapter.out.persistence;

import com.btgtechnicaltest.demo.cliente.domain.model.Cliente;
import com.btgtechnicaltest.demo.cliente.domain.ports.SaveClienteRepository;
import com.btgtechnicaltest.demo.cliente.infrastructure.adapter.out.persistence.mapper.ClienteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostgresSaveClienteRepository implements SaveClienteRepository {

    private final JpaClienteRepository jpaClienteRepository;

    @Override
    public Cliente save(Cliente cliente) {
        var entity = ClienteMapper.toEntity(cliente);
        var saved = jpaClienteRepository.save(entity);
        return ClienteMapper.toDomain(saved);
    }
}

