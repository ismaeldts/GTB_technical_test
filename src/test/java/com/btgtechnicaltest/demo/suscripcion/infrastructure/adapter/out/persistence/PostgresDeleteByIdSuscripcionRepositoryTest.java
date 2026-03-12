package com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.out.persistence;

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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostgresDeleteByIdSuscripcionRepositoryTest {

    @Mock
    private JpaSuscripcionRepository jpaSuscripcionRepository;

    @InjectMocks
    private PostgresDeleteByIdSuscripcionRepository postgresDeleteByIdSuscripcionRepository;

    @Test
    @DisplayName("Debe desactivar suscripción (soft delete) cuando existe")
    void debeDesactivarSuscripcion() {
        SuscripcionEntity entity = SuscripcionEntity.builder()
                .id(100L).clienteId(1L).fondoId(10L).monto(75000.0).activo(true).build();

        when(jpaSuscripcionRepository.findById(100L)).thenReturn(Optional.of(entity));

        postgresDeleteByIdSuscripcionRepository.deleteById(100L);

        assertThat(entity.getActivo()).isFalse();
        verify(jpaSuscripcionRepository).findById(100L);
        verify(jpaSuscripcionRepository).save(entity);
    }

    @Test
    @DisplayName("Debe lanzar excepción si suscripción no existe")
    void debeLanzarExcepcionSiNoExiste() {
        when(jpaSuscripcionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postgresDeleteByIdSuscripcionRepository.deleteById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Suscripción no encontrada con id: 99");

        verify(jpaSuscripcionRepository).findById(99L);
    }
}

