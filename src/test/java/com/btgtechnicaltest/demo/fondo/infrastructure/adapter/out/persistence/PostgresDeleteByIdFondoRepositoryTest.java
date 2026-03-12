package com.btgtechnicaltest.demo.fondo.infrastructure.adapter.out.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostgresDeleteByIdFondoRepositoryTest {

    @Mock
    private JpaFondoRepository jpaFondoRepository;

    @InjectMocks
    private PostgresDeleteByIdFondoRepository postgresDeleteByIdFondoRepository;

    @Test
    @DisplayName("Debe eliminar fondo por id delegando al JPA repository")
    void debeEliminarFondoPorId() {
        postgresDeleteByIdFondoRepository.deleteById(1L);

        verify(jpaFondoRepository).deleteById(1L);
    }
}

