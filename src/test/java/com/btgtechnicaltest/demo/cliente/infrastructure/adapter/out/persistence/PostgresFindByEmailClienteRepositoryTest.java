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
class PostgresFindByEmailClienteRepositoryTest {

    @Mock
    private JpaClienteRepository jpaClienteRepository;

    @InjectMocks
    private PostgresFindByEmailClienteRepository postgresFindByEmailClienteRepository;

    @Test
    @DisplayName("Debe retornar cliente cuando existe por email")
    void debeRetornarClienteCuandoExistePorEmail() {
        ClienteEntity entity = ClienteEntity.builder()
                .id(1L).nombre("Ismael").email("ismael@test.com")
                .apellidos("Trocha").ciudad("Bogotá").password("encoded123")
                .role("ROLE_CLIENTE").saldo(500000.0).build();

        when(jpaClienteRepository.findByEmail("ismael@test.com")).thenReturn(Optional.of(entity));

        Optional<Cliente> resultado = postgresFindByEmailClienteRepository.findByEmail("ismael@test.com");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
        assertThat(resultado.get().getEmail()).isEqualTo("ismael@test.com");
        assertThat(resultado.get().getNombre()).isEqualTo("Ismael");
        verify(jpaClienteRepository).findByEmail("ismael@test.com");
    }

    @Test
    @DisplayName("Debe retornar vacío cuando no existe por email")
    void debeRetornarVacioCuandoNoExistePorEmail() {
        when(jpaClienteRepository.findByEmail("noexiste@test.com")).thenReturn(Optional.empty());

        Optional<Cliente> resultado = postgresFindByEmailClienteRepository.findByEmail("noexiste@test.com");

        assertThat(resultado).isEmpty();
        verify(jpaClienteRepository).findByEmail("noexiste@test.com");
    }
}

