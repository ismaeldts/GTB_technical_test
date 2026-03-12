package com.btgtechnicaltest.demo.fondo.infrastructure.adapter.out.persistence;

import com.btgtechnicaltest.demo.fondo.domain.model.Fondo;
import com.btgtechnicaltest.demo.fondo.infrastructure.adapter.out.persistence.entity.FondoEntity;
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
class PostgresFindAllFondoRepositoryTest {

    @Mock
    private JpaFondoRepository jpaFondoRepository;

    @InjectMocks
    private PostgresFindAllFondoRepository postgresFindAllFondoRepository;

    @Test
    @DisplayName("Debe retornar lista de fondos")
    void debeRetornarListaDeFondos() {
        FondoEntity entity1 = FondoEntity.builder()
                .id(1L).nombre("FPV_BTG").montoMinimo(75000.0)
                .moneda("COP").categoria("FPV").build();
        FondoEntity entity2 = FondoEntity.builder()
                .id(2L).nombre("FDO_ACCIONES").montoMinimo(250000.0)
                .moneda("USD").categoria("FIC").build();

        when(jpaFondoRepository.findAll()).thenReturn(List.of(entity1, entity2));

        List<Fondo> resultado = postgresFindAllFondoRepository.findAll();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getNombre()).isEqualTo("FPV_BTG");
        assertThat(resultado.get(1).getNombre()).isEqualTo("FDO_ACCIONES");
        verify(jpaFondoRepository).findAll();
    }

    @Test
    @DisplayName("Debe retornar lista vacía si no hay fondos")
    void debeRetornarListaVacia() {
        when(jpaFondoRepository.findAll()).thenReturn(Collections.emptyList());

        List<Fondo> resultado = postgresFindAllFondoRepository.findAll();

        assertThat(resultado).isEmpty();
        verify(jpaFondoRepository).findAll();
    }
}

