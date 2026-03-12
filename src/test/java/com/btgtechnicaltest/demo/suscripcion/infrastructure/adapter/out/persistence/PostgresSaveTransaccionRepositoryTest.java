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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostgresSaveTransaccionRepositoryTest {

    @Mock
    private JpaTransaccionRepository jpaTransaccionRepository;

    @InjectMocks
    private PostgresSaveTransaccionRepository postgresSaveTransaccionRepository;

    @Test
    @DisplayName("Debe guardar transacción y retornar con datos correctos")
    void debeGuardarTransaccion() {
        UUID id = UUID.randomUUID();
        LocalDateTime fecha = LocalDateTime.of(2026, 3, 12, 10, 0);

        Transaccion transaccion = Transaccion.builder()
                .id(id)
                .clienteId(1L)
                .fondoId(10L)
                .tipo("apertura")
                .monto(75000.0)
                .fecha(fecha)
                .build();

        TransaccionEntity savedEntity = TransaccionEntity.builder()
                .id(id)
                .clienteId(1L)
                .fondoId(10L)
                .tipo("apertura")
                .monto(75000.0)
                .fecha(fecha)
                .build();

        when(jpaTransaccionRepository.save(any(TransaccionEntity.class))).thenReturn(savedEntity);

        Transaccion resultado = postgresSaveTransaccionRepository.save(transaccion);

        assertThat(resultado.getId()).isEqualTo(id);
        assertThat(resultado.getClienteId()).isEqualTo(1L);
        assertThat(resultado.getFondoId()).isEqualTo(10L);
        assertThat(resultado.getTipo()).isEqualTo("apertura");
        assertThat(resultado.getMonto()).isEqualTo(75000.0);
        assertThat(resultado.getFecha()).isEqualTo(fecha);

        verify(jpaTransaccionRepository).save(any(TransaccionEntity.class));
    }
}

