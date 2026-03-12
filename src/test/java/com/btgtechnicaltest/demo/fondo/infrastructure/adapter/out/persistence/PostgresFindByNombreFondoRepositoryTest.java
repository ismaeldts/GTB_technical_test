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
class PostgresFindByNombreFondoRepositoryTest {

    @Mock
    private JpaFondoRepository jpaFondoRepository;

    @InjectMocks
    private PostgresFindByNombreFondoRepository postgresFindByNombreFondoRepository;

    @Test
    @DisplayName("Debe retornar fondo cuando existe por nombre")
    void debeRetornarFondoCuandoExistePorNombre() {
        FondoEntity entity = FondoEntity.builder()
                .id(1L).nombre("FPV_BTG_PACTUAL_RECAUDADORA")
                .montoMinimo(75000.0).moneda("COP").categoria("FPV").build();

        when(jpaFondoRepository.findByNombre("FPV_BTG_PACTUAL_RECAUDADORA"))
                .thenReturn(Optional.of(entity));

        Optional<Fondo> resultado = postgresFindByNombreFondoRepository
                .findByNombre("FPV_BTG_PACTUAL_RECAUDADORA");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
        assertThat(resultado.get().getNombre()).isEqualTo("FPV_BTG_PACTUAL_RECAUDADORA");
        assertThat(resultado.get().getMoneda()).isEqualTo(Moneda.COP);
        verify(jpaFondoRepository).findByNombre("FPV_BTG_PACTUAL_RECAUDADORA");
    }

    @Test
    @DisplayName("Debe retornar vacío cuando fondo no existe por nombre")
    void debeRetornarVacioCuandoNoExistePorNombre() {
        when(jpaFondoRepository.findByNombre("INEXISTENTE")).thenReturn(Optional.empty());

        Optional<Fondo> resultado = postgresFindByNombreFondoRepository.findByNombre("INEXISTENTE");

        assertThat(resultado).isEmpty();
        verify(jpaFondoRepository).findByNombre("INEXISTENTE");
    }
}

