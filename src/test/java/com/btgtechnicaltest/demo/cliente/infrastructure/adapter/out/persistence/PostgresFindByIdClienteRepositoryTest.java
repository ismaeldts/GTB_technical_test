package com.btgtechnicaltest.demo.cliente.infrastructure.adapter.out.persistence;

import com.btgtechnicaltest.demo.cliente.domain.model.Cliente;
import com.btgtechnicaltest.demo.cliente.infrastructure.adapter.out.persistence.entity.ClienteEntity;
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
class PostgresFindByIdClienteRepositoryTest {

    @Mock
    private JpaClienteRepository jpaClienteRepository;

    @InjectMocks
    private PostgresFindByIdClienteRepository postgresFindByIdClienteRepository;

    @Test
    @DisplayName("Debe retornar cliente cuando existe por id")
    void debeRetornarClienteCuandoExiste() {
        ClienteEntity entity = ClienteEntity.builder()
                .id(1L)
                .nombre("Ismael")
                .email("ismael@test.com")
                .apellidos("Trocha")
                .ciudad("Bogotá")
                .password("encoded123")
                .role("ROLE_CLIENTE")
                .saldo(500000.0)
                .build();

        when(jpaClienteRepository.findById(1L)).thenReturn(Optional.of(entity));

        Optional<Cliente> resultado = postgresFindByIdClienteRepository.findById(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
        assertThat(resultado.get().getNombre()).isEqualTo("Ismael");
        assertThat(resultado.get().getEmail()).isEqualTo("ismael@test.com");
        verify(jpaClienteRepository).findById(1L);
    }

    @Test
    @DisplayName("Debe retornar vacío cuando cliente no existe")
    void debeRetornarVacioCuandoNoExiste() {
        when(jpaClienteRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Cliente> resultado = postgresFindByIdClienteRepository.findById(99L);

        assertThat(resultado).isEmpty();
        verify(jpaClienteRepository).findById(99L);
    }
}

