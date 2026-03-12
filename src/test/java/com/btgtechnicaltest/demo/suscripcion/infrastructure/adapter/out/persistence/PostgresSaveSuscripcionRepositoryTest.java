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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostgresSaveSuscripcionRepositoryTest {

    @Mock
    private JpaSuscripcionRepository jpaSuscripcionRepository;

    @InjectMocks
    private PostgresSaveSuscripcionRepository postgresSaveSuscripcionRepository;

    @Test
    @DisplayName("Debe guardar suscripción y retornar con id generado")
    void debeGuardarSuscripcion() {
        Suscripcion suscripcion = Suscripcion.builder()
                .clienteId(1L)
                .fondoId(10L)
                .monto(75000.0)
                .activo(true)
                .build();

        SuscripcionEntity savedEntity = SuscripcionEntity.builder()
                .id(100L)
                .clienteId(1L)
                .fondoId(10L)
                .monto(75000.0)
                .activo(true)
                .build();

        when(jpaSuscripcionRepository.save(any(SuscripcionEntity.class))).thenReturn(savedEntity);

        Suscripcion resultado = postgresSaveSuscripcionRepository.save(suscripcion);

        assertThat(resultado.getId()).isEqualTo(100L);
        assertThat(resultado.getClienteId()).isEqualTo(1L);
        assertThat(resultado.getFondoId()).isEqualTo(10L);
        assertThat(resultado.getMonto()).isEqualTo(75000.0);
        assertThat(resultado.getActivo()).isTrue();

        verify(jpaSuscripcionRepository).save(any(SuscripcionEntity.class));
    }
}

