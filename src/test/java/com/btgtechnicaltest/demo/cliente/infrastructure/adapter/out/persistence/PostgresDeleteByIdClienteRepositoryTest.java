package com.btgtechnicaltest.demo.cliente.infrastructure.adapter.out.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostgresDeleteByIdClienteRepositoryTest {

    @Mock
    private JpaClienteRepository jpaClienteRepository;

    @InjectMocks
    private PostgresDeleteByIdClienteRepository postgresDeleteByIdClienteRepository;

    @Test
    @DisplayName("Debe eliminar cliente por id delegando al JPA repository")
    void debeEliminarClientePorId() {
        postgresDeleteByIdClienteRepository.deleteById(1L);

        verify(jpaClienteRepository).deleteById(1L);
    }
}

