package com.btgtechnicaltest.demo.cliente.infrastructure.adapter.out.persistence;

import com.btgtechnicaltest.demo.cliente.domain.model.Cliente;
import com.btgtechnicaltest.demo.cliente.infrastructure.adapter.out.persistence.entity.ClienteEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostgresFindAllClienteRepositoryTest {

    @Mock
    private JpaClienteRepository jpaClienteRepository;

    @InjectMocks
    private PostgresFindAllClienteRepository postgresFindAllClienteRepository;

    @Test
    @DisplayName("Debe retornar lista de clientes")
    void debeRetornarListaDeClientes() {
        ClienteEntity entity1 = ClienteEntity.builder()
                .id(1L).nombre("Ismael").email("ismael@test.com")
                .apellidos("Trocha").ciudad("Bogotá").password("pass1")
                .role("ROLE_CLIENTE").saldo(500000.0).build();

        ClienteEntity entity2 = ClienteEntity.builder()
                .id(2L).nombre("Carlos").email("carlos@test.com")
                .apellidos("López").ciudad("Medellín").password("pass2")
                .role("ROLE_CLIENTE").saldo(300000.0).build();

        when(jpaClienteRepository.findAll()).thenReturn(List.of(entity1, entity2));

        List<Cliente> resultado = postgresFindAllClienteRepository.findAll();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Ismael");
        assertThat(resultado.get(1).getNombre()).isEqualTo("Carlos");
        verify(jpaClienteRepository).findAll();
    }

    @Test
    @DisplayName("Debe retornar lista vacía si no hay clientes")
    void debeRetornarListaVacia() {
        when(jpaClienteRepository.findAll()).thenReturn(Collections.emptyList());

        List<Cliente> resultado = postgresFindAllClienteRepository.findAll();

        assertThat(resultado).isEmpty();
        verify(jpaClienteRepository).findAll();
    }
}

