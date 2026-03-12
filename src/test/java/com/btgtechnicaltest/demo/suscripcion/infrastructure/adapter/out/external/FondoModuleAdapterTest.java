package com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.out.external;

import com.btgtechnicaltest.demo.fondo.domain.model.Fondo;
import com.btgtechnicaltest.demo.fondo.domain.model.Moneda;
import com.btgtechnicaltest.demo.fondo.domain.ports.FindByIdFondoRepository;
import com.btgtechnicaltest.demo.suscripcion.domain.model.FondoInfo;
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
class FondoModuleAdapterTest {

    @Mock
    private FindByIdFondoRepository findByIdFondoRepository;

    @InjectMocks
    private FondoModuleAdapter fondoModuleAdapter;

    @Test
    @DisplayName("Debe retornar FondoInfo cuando fondo existe")
    void debeRetornarFondoInfoCuandoExiste() {
        Fondo fondo = Fondo.builder()
                .id(10L)
                .nombre("FPV_BTG_PACTUAL_RECAUDADORA")
                .montoMinimo(75000.0)
                .moneda(Moneda.COP)
                .categoria("FPV")
                .build();

        when(findByIdFondoRepository.findById(10L)).thenReturn(Optional.of(fondo));

        Optional<FondoInfo> resultado = fondoModuleAdapter.findById(10L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(10L);
        assertThat(resultado.get().getNombre()).isEqualTo("FPV_BTG_PACTUAL_RECAUDADORA");
        assertThat(resultado.get().getMontoMinimo()).isEqualTo(75000.0);
        assertThat(resultado.get().getCategoria()).isEqualTo("FPV");
        verify(findByIdFondoRepository).findById(10L);
    }

    @Test
    @DisplayName("Debe retornar vacío cuando fondo no existe")
    void debeRetornarVacioCuandoNoExiste() {
        when(findByIdFondoRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<FondoInfo> resultado = fondoModuleAdapter.findById(99L);

        assertThat(resultado).isEmpty();
        verify(findByIdFondoRepository).findById(99L);
    }
}

