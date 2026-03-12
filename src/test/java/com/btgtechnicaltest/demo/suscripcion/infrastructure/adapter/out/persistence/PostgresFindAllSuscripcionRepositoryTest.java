package com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.out.persistence;

import com.btgtechnicaltest.demo.suscripcion.domain.model.Suscripcion;
import com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.out.persistence.entity.SuscripcionEntity;
import com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.out.persistence.repository.JpaSuscripcionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostgresFindAllSuscripcionRepositoryTest {

    @Mock
    private JpaSuscripcionRepository jpaSuscripcionRepository;

    @InjectMocks
    private PostgresFindAllSuscripcionRepository postgresFindAllSuscripcionRepository;

    @Test
    @DisplayName("Debe retornar lista de suscripciones activas")
    void debeRetornarListaDeSuscripcionesActivas() {
        SuscripcionEntity entity1 = SuscripcionEntity.builder()
                .id(1L).clienteId(1L).fondoId(10L).monto(75000.0).activo(true).build();
        SuscripcionEntity entity2 = SuscripcionEntity.builder()
                .id(2L).clienteId(2L).fondoId(20L).monto(100000.0).activo(true).build();

        when(jpaSuscripcionRepository.findByActivoTrue()).thenReturn(List.of(entity1, entity2));

        List<Suscripcion> resultado = postgresFindAllSuscripcionRepository.findAll();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getId()).isEqualTo(1L);
        assertThat(resultado.get(1).getId()).isEqualTo(2L);
        verify(jpaSuscripcionRepository).findByActivoTrue();
    }

    @Test
    @DisplayName("Debe retornar lista vacía si no hay suscripciones activas")
    void debeRetornarListaVacia() {
        when(jpaSuscripcionRepository.findByActivoTrue()).thenReturn(Collections.emptyList());

        List<Suscripcion> resultado = postgresFindAllSuscripcionRepository.findAll();

        assertThat(resultado).isEmpty();
        verify(jpaSuscripcionRepository).findByActivoTrue();
    }
}

