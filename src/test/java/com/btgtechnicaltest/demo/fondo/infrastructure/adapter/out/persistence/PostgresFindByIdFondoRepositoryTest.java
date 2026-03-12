package com.btgtechnicaltest.demo.fondo.infrastructure.adapter.out.persistence;

import com.btgtechnicaltest.demo.fondo.domain.model.Fondo;
import com.btgtechnicaltest.demo.fondo.domain.model.Moneda;
import com.btgtechnicaltest.demo.fondo.infrastructure.adapter.out.persistence.entity.FondoEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostgresFindByIdFondoRepositoryTest {

    @Mock
    private JpaFondoRepository jpaFondoRepository;

    @InjectMocks
    private PostgresFindByIdFondoRepository postgresFindByIdFondoRepository;

    @Test
    @DisplayName("Debe retornar fondo cuando existe por id")
    void debeRetornarFondoCuandoExiste() {
        FondoEntity entity = FondoEntity.builder()
                .id(1L).nombre("FPV_BTG_PACTUAL_RECAUDADORA")
                .montoMinimo(75000.0).moneda("COP").categoria("FPV").build();

        when(jpaFondoRepository.findById(1L)).thenReturn(Optional.of(entity));

        Optional<Fondo> resultado = postgresFindByIdFondoRepository.findById(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
        assertThat(resultado.get().getNombre()).isEqualTo("FPV_BTG_PACTUAL_RECAUDADORA");
        assertThat(resultado.get().getMoneda()).isEqualTo(Moneda.COP);
        verify(jpaFondoRepository).findById(1L);
    }

    @Test
    @DisplayName("Debe retornar vacío cuando fondo no existe")
    void debeRetornarVacioCuandoNoExiste() {
        when(jpaFondoRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Fondo> resultado = postgresFindByIdFondoRepository.findById(99L);

        assertThat(resultado).isEmpty();
        verify(jpaFondoRepository).findById(99L);
    }
}

