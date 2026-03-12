package com.btgtechnicaltest.demo.cliente.infrastructure.adapter.out.persistence;

import com.btgtechnicaltest.demo.cliente.domain.model.Cliente;
import com.btgtechnicaltest.demo.cliente.infrastructure.adapter.out.persistence.entity.ClienteEntity;
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
class PostgresSaveClienteRepositoryTest {

    @Mock
    private JpaClienteRepository jpaClienteRepository;

    @InjectMocks
    private PostgresSaveClienteRepository postgresSaveClienteRepository;

    @Test
    @DisplayName("Debe guardar cliente y retornar el cliente con id generado")
    void debeGuardarCliente() {
        Cliente cliente = Cliente.builder()
                .nombre("Ismael")
                .email("ismael@test.com")
                .apellidos("Trocha")
                .ciudad("Bogotá")
                .password("encoded123")
                .role("ROLE_CLIENTE")
                .saldo(500000.0)
                .build();

        ClienteEntity savedEntity = ClienteEntity.builder()
                .id(1L)
                .nombre("Ismael")
                .email("ismael@test.com")
                .apellidos("Trocha")
                .ciudad("Bogotá")
                .password("encoded123")
                .role("ROLE_CLIENTE")
                .saldo(500000.0)
                .build();

        when(jpaClienteRepository.save(any(ClienteEntity.class))).thenReturn(savedEntity);

        Cliente resultado = postgresSaveClienteRepository.save(cliente);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Ismael");
        assertThat(resultado.getEmail()).isEqualTo("ismael@test.com");
        assertThat(resultado.getApellidos()).isEqualTo("Trocha");
        assertThat(resultado.getCiudad()).isEqualTo("Bogotá");
        assertThat(resultado.getPassword()).isEqualTo("encoded123");
        assertThat(resultado.getRole()).isEqualTo("ROLE_CLIENTE");
        assertThat(resultado.getSaldo()).isEqualTo(500000.0);

        verify(jpaClienteRepository).save(any(ClienteEntity.class));
    }
}

