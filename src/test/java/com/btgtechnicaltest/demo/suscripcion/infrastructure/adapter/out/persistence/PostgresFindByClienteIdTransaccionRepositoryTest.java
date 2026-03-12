package com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.out.persistence;

import com.btgtechnicaltest.demo.suscripcion.domain.model.Transaccion;
import com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.out.persistence.entity.TransaccionEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostgresFindByClienteIdTransaccionRepositoryTest {

    @Mock
    private JpaTransaccionRepository jpaTransaccionRepository;

    @InjectMocks
    private PostgresFindByClienteIdTransaccionRepository postgresFindByClienteIdTransaccionRepository;

    @Test
    @DisplayName("Debe retornar transacciones ordenadas por fecha descendente")
    void debeRetornarTransaccionesPorClienteId() {
        LocalDateTime fecha1 = LocalDateTime.of(2026, 3, 12, 10, 0);
        LocalDateTime fecha2 = LocalDateTime.of(2026, 3, 11, 9, 0);

        TransaccionEntity entity1 = TransaccionEntity.builder()
                .id(UUID.randomUUID()).clienteId(1L).fondoId(10L)
                .tipo("apertura").monto(75000.0).fecha(fecha1).build();
        TransaccionEntity entity2 = TransaccionEntity.builder()
                .id(UUID.randomUUID()).clienteId(1L).fondoId(20L)
                .tipo("cancelacion").monto(100000.0).fecha(fecha2).build();

        when(jpaTransaccionRepository.findByClienteIdOrderByFechaDesc(1L))
                .thenReturn(List.of(entity1, entity2));

        List<Transaccion> resultado = postgresFindByClienteIdTransaccionRepository.findByClienteId(1L);

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getTipo()).isEqualTo("apertura");
        assertThat(resultado.get(1).getTipo()).isEqualTo("cancelacion");
        verify(jpaTransaccionRepository).findByClienteIdOrderByFechaDesc(1L);
    }

    @Test
    @DisplayName("Debe retornar lista vacía si no hay transacciones")
    void debeRetornarListaVacia() {
        when(jpaTransaccionRepository.findByClienteIdOrderByFechaDesc(99L))
                .thenReturn(Collections.emptyList());

        List<Transaccion> resultado = postgresFindByClienteIdTransaccionRepository.findByClienteId(99L);

        assertThat(resultado).isEmpty();
        verify(jpaTransaccionRepository).findByClienteIdOrderByFechaDesc(99L);
    }
}

