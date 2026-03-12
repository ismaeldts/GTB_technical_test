package com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.out.external;

import com.btgtechnicaltest.demo.cliente.domain.model.Cliente;
import com.btgtechnicaltest.demo.cliente.domain.ports.FindByEmailClienteRepository;
import com.btgtechnicaltest.demo.cliente.domain.ports.FindByIdClienteRepository;
import com.btgtechnicaltest.demo.cliente.domain.ports.SaveClienteRepository;
import com.btgtechnicaltest.demo.suscripcion.domain.model.ClienteInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteModuleAdapterTest {

    @Mock
    private FindByIdClienteRepository findByIdClienteRepository;
    @Mock
    private FindByEmailClienteRepository findByEmailClienteRepository;
    @Mock
    private SaveClienteRepository saveClienteRepository;

    @InjectMocks
    private ClienteModuleAdapter clienteModuleAdapter;

    private Cliente crearCliente() {
        return Cliente.builder()
                .id(1L).nombre("Ismael").email("ismael@test.com")
                .apellidos("Trocha").ciudad("Bogotá").password("encoded123")
                .role("ROLE_CLIENTE").saldo(500000.0).build();
    }

    @Nested
    @DisplayName("existsById()")
    class ExistsById {

        @Test
        @DisplayName("Debe retornar true si cliente existe")
        void debeRetornarTrueSiExiste() {
            when(findByIdClienteRepository.findById(1L))
                    .thenReturn(Optional.of(crearCliente()));

            assertThat(clienteModuleAdapter.existsById(1L)).isTrue();
            verify(findByIdClienteRepository).findById(1L);
        }

        @Test
        @DisplayName("Debe retornar false si cliente no existe")
        void debeRetornarFalseSiNoExiste() {
            when(findByIdClienteRepository.findById(99L))
                    .thenReturn(Optional.empty());

            assertThat(clienteModuleAdapter.existsById(99L)).isFalse();
            verify(findByIdClienteRepository).findById(99L);
        }
    }

    @Nested
    @DisplayName("findByEmail()")
    class FindByEmail {

        @Test
        @DisplayName("Debe retornar ClienteInfo cuando existe por email")
        void debeRetornarClienteInfoCuandoExiste() {
            when(findByEmailClienteRepository.findByEmail("ismael@test.com"))
                    .thenReturn(Optional.of(crearCliente()));

            Optional<ClienteInfo> resultado = clienteModuleAdapter.findByEmail("ismael@test.com");

            assertThat(resultado).isPresent();
            assertThat(resultado.get().getId()).isEqualTo(1L);
            assertThat(resultado.get().getEmail()).isEqualTo("ismael@test.com");
            assertThat(resultado.get().getSaldo()).isEqualTo(500000.0);
        }

        @Test
        @DisplayName("Debe retornar vacío cuando no existe por email")
        void debeRetornarVacioCuandoNoExiste() {
            when(findByEmailClienteRepository.findByEmail("noexiste@test.com"))
                    .thenReturn(Optional.empty());

            Optional<ClienteInfo> resultado = clienteModuleAdapter.findByEmail("noexiste@test.com");

            assertThat(resultado).isEmpty();
        }
    }

    @Nested
    @DisplayName("debitarSaldo()")
    class DebitarSaldo {

        @Test
        @DisplayName("Debe debitar saldo correctamente")
        void debeDebitarSaldo() {
            Cliente cliente = crearCliente();
            when(findByIdClienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
            when(saveClienteRepository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

            clienteModuleAdapter.debitarSaldo(1L, 75000.0);

            assertThat(cliente.getSaldo()).isEqualTo(425000.0);
            verify(saveClienteRepository).save(cliente);
        }

        @Test
        @DisplayName("Debe lanzar excepción si cliente no existe al debitar")
        void debeLanzarExcepcionSiNoExiste() {
            when(findByIdClienteRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> clienteModuleAdapter.debitarSaldo(99L, 75000.0))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Cliente no encontrado con id: 99");

            verify(saveClienteRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("acreditarSaldo()")
    class AcreditarSaldo {

        @Test
        @DisplayName("Debe acreditar saldo correctamente")
        void debeAcreditarSaldo() {
            Cliente cliente = crearCliente();
            when(findByIdClienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
            when(saveClienteRepository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

            clienteModuleAdapter.acreditarSaldo(1L, 75000.0);

            assertThat(cliente.getSaldo()).isEqualTo(575000.0);
            verify(saveClienteRepository).save(cliente);
        }

        @Test
        @DisplayName("Debe lanzar excepción si cliente no existe al acreditar")
        void debeLanzarExcepcionSiNoExiste() {
            when(findByIdClienteRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> clienteModuleAdapter.acreditarSaldo(99L, 75000.0))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Cliente no encontrado con id: 99");

            verify(saveClienteRepository, never()).save(any());
        }
    }
}

