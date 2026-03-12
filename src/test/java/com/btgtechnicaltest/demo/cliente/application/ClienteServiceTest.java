package com.btgtechnicaltest.demo.cliente.application;

import com.btgtechnicaltest.demo.cliente.domain.exception.ClienteAlreadyExistsException;
import com.btgtechnicaltest.demo.cliente.domain.model.Cliente;
import com.btgtechnicaltest.demo.cliente.domain.ports.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private SaveClienteRepository saveClienteRepository;
    @Mock
    private FindByIdClienteRepository findByIdClienteRepository;
    @Mock
    private FindAllClienteRepository findAllClienteRepository;
    @Mock
    private DeleteByIdClienteRepository deleteByIdClienteRepository;
    @Mock
    private FindByEmailClienteRepository findByEmailClienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder()
                .id(1L)
                .nombre("Ismael")
                .apellidos("Trocha")
                .email("ismael@test.com")
                .ciudad("Bogotá")
                .password("encoded")
                .role("ROLE_CLIENTE")
                .saldo(500000.0)
                .build();
    }

    @Nested
    @DisplayName("crear()")
    class Crear {

        @Test
        @DisplayName("Debe crear cliente con saldo inicial de 500.000")
        void debeCrearClienteConSaldoInicial() {
            when(findByEmailClienteRepository.findByEmail(cliente.getEmail()))
                    .thenReturn(Optional.empty());
            when(saveClienteRepository.save(any(Cliente.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            Cliente resultado = clienteService.crear(cliente);

            assertThat(resultado.getSaldo()).isEqualTo(500000.0);
            verify(saveClienteRepository).save(cliente);
        }

        @Test
        @DisplayName("Debe lanzar excepción si el email ya existe")
        void debeLanzarExcepcionSiEmailDuplicado() {
            when(findByEmailClienteRepository.findByEmail(cliente.getEmail()))
                    .thenReturn(Optional.of(cliente));

            assertThatThrownBy(() -> clienteService.crear(cliente))
                    .isInstanceOf(ClienteAlreadyExistsException.class)
                    .hasMessageContaining(cliente.getEmail());

            verify(saveClienteRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("obtenerPorId()")
    class ObtenerPorId {

        @Test
        @DisplayName("Debe retornar cliente cuando existe")
        void debeRetornarClienteCuandoExiste() {
            when(findByIdClienteRepository.findById(1L))
                    .thenReturn(Optional.of(cliente));

            Optional<Cliente> resultado = clienteService.obtenerPorId(1L);

            assertThat(resultado).isPresent();
            assertThat(resultado.get().getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Debe retornar vacío cuando no existe")
        void debeRetornarVacioCuandoNoExiste() {
            when(findByIdClienteRepository.findById(99L))
                    .thenReturn(Optional.empty());

            Optional<Cliente> resultado = clienteService.obtenerPorId(99L);

            assertThat(resultado).isEmpty();
        }
    }

    @Nested
    @DisplayName("obtenerTodos()")
    class ObtenerTodos {

        @Test
        @DisplayName("Debe retornar lista de clientes")
        void debeRetornarListaDeClientes() {
            when(findAllClienteRepository.findAll())
                    .thenReturn(List.of(cliente));

            List<Cliente> resultado = clienteService.obtenerTodos();

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getEmail()).isEqualTo("ismael@test.com");
        }
    }

    @Nested
    @DisplayName("actualizar()")
    class Actualizar {

        @Test
        @DisplayName("Debe actualizar cliente existente")
        void debeActualizarClienteExistente() {
            when(findByIdClienteRepository.findById(1L))
                    .thenReturn(Optional.of(cliente));
            when(findByEmailClienteRepository.findByEmail(cliente.getEmail()))
                    .thenReturn(Optional.of(cliente));
            when(saveClienteRepository.save(any(Cliente.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            cliente.setNombre("Ismael Actualizado");
            Cliente resultado = clienteService.actualizar(cliente);

            assertThat(resultado.getNombre()).isEqualTo("Ismael Actualizado");
            verify(saveClienteRepository).save(cliente);
        }

        @Test
        @DisplayName("Debe lanzar excepción si cliente no existe")
        void debeLanzarExcepcionSiClienteNoExiste() {
            when(findByIdClienteRepository.findById(99L))
                    .thenReturn(Optional.empty());

            cliente.setId(99L);

            assertThatThrownBy(() -> clienteService.actualizar(cliente))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Cliente no encontrado");
        }

        @Test
        @DisplayName("Debe lanzar excepción si email ya pertenece a otro cliente")
        void debeLanzarExcepcionSiEmailDeOtroCliente() {
            Cliente otroCliente = Cliente.builder().id(2L).email("ismael@test.com").build();

            when(findByIdClienteRepository.findById(1L))
                    .thenReturn(Optional.of(cliente));
            when(findByEmailClienteRepository.findByEmail(cliente.getEmail()))
                    .thenReturn(Optional.of(otroCliente));

            assertThatThrownBy(() -> clienteService.actualizar(cliente))
                    .isInstanceOf(ClienteAlreadyExistsException.class)
                    .hasMessageContaining("Ya existe otro cliente");
        }
    }

    @Nested
    @DisplayName("eliminar()")
    class Eliminar {

        @Test
        @DisplayName("Debe eliminar cliente existente")
        void debeEliminarClienteExistente() {
            when(findByIdClienteRepository.findById(1L))
                    .thenReturn(Optional.of(cliente));

            clienteService.eliminar(1L);

            verify(deleteByIdClienteRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Debe lanzar excepción si cliente no existe")
        void debeLanzarExcepcionSiClienteNoExiste() {
            when(findByIdClienteRepository.findById(99L))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> clienteService.eliminar(99L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Cliente no encontrado");
        }
    }

    @Nested
    @DisplayName("recargarSaldo()")
    class RecargarSaldo {

        @Test
        @DisplayName("Debe recargar saldo correctamente")
        void debeRecargarSaldoCorrectamente() {
            when(findByEmailClienteRepository.findByEmail("ismael@test.com"))
                    .thenReturn(Optional.of(cliente));
            when(saveClienteRepository.save(any(Cliente.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            Cliente resultado = clienteService.recargarSaldo("ismael@test.com", 100000.0);

            assertThat(resultado.getSaldo()).isEqualTo(600000.0);
            verify(saveClienteRepository).save(cliente);
        }

        @Test
        @DisplayName("Debe lanzar excepción si el monto es cero")
        void debeLanzarExcepcionSiMontoCero() {
            when(findByEmailClienteRepository.findByEmail("ismael@test.com"))
                    .thenReturn(Optional.of(cliente));

            assertThatThrownBy(() -> clienteService.recargarSaldo("ismael@test.com", 0.0))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("mayor a 0");
        }

        @Test
        @DisplayName("Debe lanzar excepción si el monto es negativo")
        void debeLanzarExcepcionSiMontoNegativo() {
            when(findByEmailClienteRepository.findByEmail("ismael@test.com"))
                    .thenReturn(Optional.of(cliente));

            assertThatThrownBy(() -> clienteService.recargarSaldo("ismael@test.com", -50000.0))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("mayor a 0");
        }

        @Test
        @DisplayName("Debe lanzar excepción si cliente no existe")
        void debeLanzarExcepcionSiClienteNoExiste() {
            when(findByEmailClienteRepository.findByEmail("noexiste@test.com"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> clienteService.recargarSaldo("noexiste@test.com", 100000.0))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Cliente no encontrado");
        }
    }
}

