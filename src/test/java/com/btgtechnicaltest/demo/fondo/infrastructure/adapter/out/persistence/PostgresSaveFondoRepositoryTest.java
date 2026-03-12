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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostgresSaveFondoRepositoryTest {

    @Mock
    private JpaFondoRepository jpaFondoRepository;

    @InjectMocks
    private PostgresSaveFondoRepository postgresSaveFondoRepository;

    @Test
    @DisplayName("Debe guardar fondo y retornar el fondo con id generado")
    void debeGuardarFondo() {
        Fondo fondo = Fondo.builder()
                .nombre("FPV_BTG_PACTUAL_RECAUDADORA")
                .montoMinimo(75000.0)
                .moneda(Moneda.COP)
                .categoria("FPV")
                .build();

        FondoEntity savedEntity = FondoEntity.builder()
                .id(1L)
                .nombre("FPV_BTG_PACTUAL_RECAUDADORA")
                .montoMinimo(75000.0)
                .moneda("COP")
                .categoria("FPV")
                .build();

        when(jpaFondoRepository.save(any(FondoEntity.class))).thenReturn(savedEntity);

        Fondo resultado = postgresSaveFondoRepository.save(fondo);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("FPV_BTG_PACTUAL_RECAUDADORA");
        assertThat(resultado.getMontoMinimo()).isEqualTo(75000.0);
        assertThat(resultado.getMoneda()).isEqualTo(Moneda.COP);
        assertThat(resultado.getCategoria()).isEqualTo("FPV");

        verify(jpaFondoRepository).save(any(FondoEntity.class));
    }
}

