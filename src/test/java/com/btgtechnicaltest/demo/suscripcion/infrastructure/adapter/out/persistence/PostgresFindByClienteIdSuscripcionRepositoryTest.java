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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostgresFindByClienteIdSuscripcionRepositoryTest {

    @Mock
    private JpaSuscripcionRepository jpaSuscripcionRepository;

    @InjectMocks
    private PostgresFindByClienteIdSuscripcionRepository postgresFindByClienteIdSuscripcionRepository;

    @Test
    @DisplayName("Debe retornar suscripciones activas por clienteId")
    void debeRetornarSuscripcionesPorClienteId() {
        SuscripcionEntity entity1 = SuscripcionEntity.builder()
                .id(1L).clienteId(1L).fondoId(10L).monto(75000.0).activo(true).build();
        SuscripcionEntity entity2 = SuscripcionEntity.builder()
                .id(2L).clienteId(1L).fondoId(20L).monto(100000.0).activo(true).build();

        when(jpaSuscripcionRepository.findByClienteIdAndActivoTrue(1L))
                .thenReturn(List.of(entity1, entity2));

        List<Suscripcion> resultado = postgresFindByClienteIdSuscripcionRepository.findByClienteId(1L);

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getClienteId()).isEqualTo(1L);
        assertThat(resultado.get(1).getFondoId()).isEqualTo(20L);
        verify(jpaSuscripcionRepository).findByClienteIdAndActivoTrue(1L);
    }

    @Test
    @DisplayName("Debe retornar lista vacía si cliente no tiene suscripciones activas")
    void debeRetornarListaVacia() {
        when(jpaSuscripcionRepository.findByClienteIdAndActivoTrue(99L))
                .thenReturn(Collections.emptyList());

        List<Suscripcion> resultado = postgresFindByClienteIdSuscripcionRepository.findByClienteId(99L);

        assertThat(resultado).isEmpty();
        verify(jpaSuscripcionRepository).findByClienteIdAndActivoTrue(99L);
    }

    @Test
    @DisplayName("Debe retornar suscripción cuando existe por clienteId y fondoId")
    void debeRetornarSuscripcionPorClienteIdYFondoId() {
        SuscripcionEntity entity = SuscripcionEntity.builder()
                .id(1L).clienteId(1L).fondoId(10L).monto(75000.0).activo(true).build();

        when(jpaSuscripcionRepository.findByClienteIdAndFondoIdAndActivoTrue(1L, 10L))
                .thenReturn(Optional.of(entity));

        Optional<Suscripcion> resultado = postgresFindByClienteIdSuscripcionRepository
                .findByClienteIdAndFondoId(1L, 10L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getClienteId()).isEqualTo(1L);
        assertThat(resultado.get().getFondoId()).isEqualTo(10L);
        verify(jpaSuscripcionRepository).findByClienteIdAndFondoIdAndActivoTrue(1L, 10L);
    }

    @Test
    @DisplayName("Debe retornar vacío cuando no existe suscripción por clienteId y fondoId")
    void debeRetornarVacioPorClienteIdYFondoId() {
        when(jpaSuscripcionRepository.findByClienteIdAndFondoIdAndActivoTrue(1L, 99L))
                .thenReturn(Optional.empty());

        Optional<Suscripcion> resultado = postgresFindByClienteIdSuscripcionRepository
                .findByClienteIdAndFondoId(1L, 99L);

        assertThat(resultado).isEmpty();
        verify(jpaSuscripcionRepository).findByClienteIdAndFondoIdAndActivoTrue(1L, 99L);
    }
}

