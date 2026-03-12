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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostgresFindByIdSuscripcionRepositoryTest {

    @Mock
    private JpaSuscripcionRepository jpaSuscripcionRepository;

    @InjectMocks
    private PostgresFindByIdSuscripcionRepository postgresFindByIdSuscripcionRepository;

    @Test
    @DisplayName("Debe retornar suscripción activa cuando existe por id")
    void debeRetornarSuscripcionActivaCuandoExiste() {
        SuscripcionEntity entity = SuscripcionEntity.builder()
                .id(100L).clienteId(1L).fondoId(10L).monto(75000.0).activo(true).build();

        when(jpaSuscripcionRepository.findByIdAndActivoTrue(100L)).thenReturn(Optional.of(entity));

        Optional<Suscripcion> resultado = postgresFindByIdSuscripcionRepository.findById(100L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(100L);
        assertThat(resultado.get().getClienteId()).isEqualTo(1L);
        assertThat(resultado.get().getFondoId()).isEqualTo(10L);
        assertThat(resultado.get().getActivo()).isTrue();
        verify(jpaSuscripcionRepository).findByIdAndActivoTrue(100L);
    }

    @Test
    @DisplayName("Debe retornar vacío cuando suscripción no existe o no está activa")
    void debeRetornarVacioCuandoNoExiste() {
        when(jpaSuscripcionRepository.findByIdAndActivoTrue(99L)).thenReturn(Optional.empty());

        Optional<Suscripcion> resultado = postgresFindByIdSuscripcionRepository.findById(99L);

        assertThat(resultado).isEmpty();
        verify(jpaSuscripcionRepository).findByIdAndActivoTrue(99L);
    }
}

