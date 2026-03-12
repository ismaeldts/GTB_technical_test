package com.btgtechnicaltest.demo.suscripcion.application;

import com.btgtechnicaltest.demo.suscripcion.domain.exception.*;
import com.btgtechnicaltest.demo.suscripcion.domain.model.ClienteInfo;
import com.btgtechnicaltest.demo.suscripcion.domain.model.FondoInfo;
import com.btgtechnicaltest.demo.suscripcion.domain.model.Suscripcion;
import com.btgtechnicaltest.demo.suscripcion.domain.model.Transaccion;
import com.btgtechnicaltest.demo.suscripcion.domain.ports.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SuscripcionServiceTest {

    @Mock
    private SaveSuscripcionRepository saveSuscripcionRepository;
    @Mock
    private FindByIdSuscripcionRepository findByIdSuscripcionRepository;
    @Mock
    private FindAllSuscripcionRepository findAllSuscripcionRepository;
    @Mock
    private FindByClienteIdSuscripcionRepository findByClienteIdSuscripcionRepository;
    @Mock
    private DeleteByIdSuscripcionRepository deleteByIdSuscripcionRepository;
    @Mock
    private SaveTransaccionRepository saveTransaccionRepository;
    @Mock
    private FindByClienteIdTransaccionRepository findByClienteIdTransaccionRepository;
    @Mock
    private ClienteExternalPort clienteExternalPort;
    @Mock
    private FondoExternalPort fondoExternalPort;

    @InjectMocks
    private SuscripcionService suscripcionService;

    private ClienteInfo cliente;
    private FondoInfo fondo;

    @BeforeEach
    void setUp() {
        cliente = ClienteInfo.builder()
                .id(1L)
                .email("ismael@test.com")
                .saldo(500000.0)
                .build();

        fondo = FondoInfo.builder()
                .id(10L)
                .nombre("FPV_BTG_PACTUAL_RECAUDADORA")
                .montoMinimo(75000.0)
                .categoria("FPV")
                .build();
    }

    @Nested
    @DisplayName("suscribir()")
    class Suscribir {

        @Test
        @DisplayName("Debe suscribir exitosamente y debitar saldo")
        void debeSuscribirExitosamente() {
            when(clienteExternalPort.findByEmail("ismael@test.com"))
                    .thenReturn(Optional.of(cliente));
            when(fondoExternalPort.findById(10L))
                    .thenReturn(Optional.of(fondo));
            when(findByClienteIdSuscripcionRepository.findByClienteIdAndFondoId(1L, 10L))
                    .thenReturn(Optional.empty());
            when(saveSuscripcionRepository.save(any(Suscripcion.class)))
                    .thenAnswer(inv -> {
                        Suscripcion s = inv.getArgument(0);
                        s.setId(100L);
                        return s;
                    });
            when(saveTransaccionRepository.save(any(Transaccion.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            Suscripcion resultado = suscripcionService.suscribir("ismael@test.com", 10L);

            assertThat(resultado.getClienteId()).isEqualTo(1L);
            assertThat(resultado.getFondoId()).isEqualTo(10L);
            assertThat(resultado.getMonto()).isEqualTo(75000.0);
            assertThat(resultado.getFondoNombre()).isEqualTo("FPV_BTG_PACTUAL_RECAUDADORA");
            assertThat(resultado.getFondoCategoria()).isEqualTo("FPV");

            verify(clienteExternalPort).debitarSaldo(1L, 75000.0);
            verify(saveSuscripcionRepository).save(any(Suscripcion.class));
            verify(saveTransaccionRepository).save(any(Transaccion.class));
        }

        @Test
        @DisplayName("Debe lanzar excepción si cliente no encontrado")
        void debeLanzarExcepcionSiClienteNoExiste() {
            when(clienteExternalPort.findByEmail("noexiste@test.com"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> suscripcionService.suscribir("noexiste@test.com", 10L))
                    .isInstanceOf(ClienteNotFoundException.class)
                    .hasMessageContaining("noexiste@test.com");

            verify(saveSuscripcionRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe lanzar excepción si fondo no encontrado")
        void debeLanzarExcepcionSiFondoNoExiste() {
            when(clienteExternalPort.findByEmail("ismael@test.com"))
                    .thenReturn(Optional.of(cliente));
            when(fondoExternalPort.findById(99L))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> suscripcionService.suscribir("ismael@test.com", 99L))
                    .isInstanceOf(FondoNotFoundException.class);

            verify(saveSuscripcionRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe lanzar excepción si ya está suscrito al fondo")
        void debeLanzarExcepcionSiSuscripcionDuplicada() {
            Suscripcion existente = Suscripcion.builder().id(50L).clienteId(1L).fondoId(10L).build();

            when(clienteExternalPort.findByEmail("ismael@test.com"))
                    .thenReturn(Optional.of(cliente));
            when(fondoExternalPort.findById(10L))
                    .thenReturn(Optional.of(fondo));
            when(findByClienteIdSuscripcionRepository.findByClienteIdAndFondoId(1L, 10L))
                    .thenReturn(Optional.of(existente));

            assertThatThrownBy(() -> suscripcionService.suscribir("ismael@test.com", 10L))
                    .isInstanceOf(SuscripcionDuplicadaException.class)
                    .hasMessageContaining("FPV_BTG_PACTUAL_RECAUDADORA");

            verify(saveSuscripcionRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe lanzar excepción si saldo insuficiente")
        void debeLanzarExcepcionSiSaldoInsuficiente() {
            ClienteInfo clientePobre = ClienteInfo.builder()
                    .id(2L)
                    .email("pobre@test.com")
                    .saldo(10000.0)
                    .build();

            FondoInfo fondoCaro = FondoInfo.builder()
                    .id(4L)
                    .nombre("FDO_ACCIONES")
                    .montoMinimo(250000.0)
                    .categoria("FIC")
                    .build();

            when(clienteExternalPort.findByEmail("pobre@test.com"))
                    .thenReturn(Optional.of(clientePobre));
            when(fondoExternalPort.findById(4L))
                    .thenReturn(Optional.of(fondoCaro));
            when(findByClienteIdSuscripcionRepository.findByClienteIdAndFondoId(2L, 4L))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> suscripcionService.suscribir("pobre@test.com", 4L))
                    .isInstanceOf(SaldoInsuficienteException.class)
                    .hasMessageContaining("FDO_ACCIONES");

            verify(clienteExternalPort, never()).debitarSaldo(any(), any());
            verify(saveSuscripcionRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("cancelar()")
    class Cancelar {

        @Test
        @DisplayName("Debe cancelar suscripción y acreditar saldo")
        void debeCancelarYAcreditarSaldo() {
            Suscripcion suscripcion = Suscripcion.builder()
                    .id(100L)
                    .clienteId(1L)
                    .fondoId(10L)
                    .monto(75000.0)
                    .activo(true)
                    .build();

            when(clienteExternalPort.findByEmail("ismael@test.com"))
                    .thenReturn(Optional.of(cliente));
            when(findByIdSuscripcionRepository.findById(100L))
                    .thenReturn(Optional.of(suscripcion));
            when(fondoExternalPort.findById(10L))
                    .thenReturn(Optional.of(fondo));
            when(saveTransaccionRepository.save(any(Transaccion.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            Double montoDevuelto = suscripcionService.cancelar("ismael@test.com", 100L);

            assertThat(montoDevuelto).isEqualTo(75000.0);
            verify(clienteExternalPort).acreditarSaldo(1L, 75000.0);
            verify(deleteByIdSuscripcionRepository).deleteById(100L);
            verify(saveTransaccionRepository).save(any(Transaccion.class));
        }

        @Test
        @DisplayName("Debe lanzar excepción si cliente no encontrado")
        void debeLanzarExcepcionSiClienteNoExiste() {
            when(clienteExternalPort.findByEmail("noexiste@test.com"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> suscripcionService.cancelar("noexiste@test.com", 100L))
                    .isInstanceOf(ClienteNotFoundException.class);

            verify(deleteByIdSuscripcionRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("Debe lanzar excepción si suscripción no encontrada")
        void debeLanzarExcepcionSiSuscripcionNoExiste() {
            when(clienteExternalPort.findByEmail("ismael@test.com"))
                    .thenReturn(Optional.of(cliente));
            when(findByIdSuscripcionRepository.findById(999L))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> suscripcionService.cancelar("ismael@test.com", 999L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Suscripción no encontrada");

            verify(clienteExternalPort, never()).acreditarSaldo(any(), any());
        }

        @Test
        @DisplayName("Debe lanzar excepción si fondo de la suscripción no existe")
        void debeLanzarExcepcionSiFondoNoExiste() {
            Suscripcion suscripcion = Suscripcion.builder()
                    .id(100L).clienteId(1L).fondoId(99L).monto(75000.0).build();

            when(clienteExternalPort.findByEmail("ismael@test.com"))
                    .thenReturn(Optional.of(cliente));
            when(findByIdSuscripcionRepository.findById(100L))
                    .thenReturn(Optional.of(suscripcion));
            when(fondoExternalPort.findById(99L))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> suscripcionService.cancelar("ismael@test.com", 100L))
                    .isInstanceOf(FondoNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("obtenerPorCliente()")
    class ObtenerPorCliente {

        @Test
        @DisplayName("Debe retornar suscripciones enriquecidas con info del fondo")
        void debeRetornarSuscripcionesEnriquecidas() {
            Suscripcion sus = Suscripcion.builder()
                    .id(1L).clienteId(1L).fondoId(10L).monto(75000.0).activo(true).build();

            when(clienteExternalPort.findByEmail("ismael@test.com"))
                    .thenReturn(Optional.of(cliente));
            when(findByClienteIdSuscripcionRepository.findByClienteId(1L))
                    .thenReturn(List.of(sus));
            when(fondoExternalPort.findById(10L))
                    .thenReturn(Optional.of(fondo));

            List<Suscripcion> resultado = suscripcionService.obtenerPorCliente("ismael@test.com");

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getFondoNombre()).isEqualTo("FPV_BTG_PACTUAL_RECAUDADORA");
            assertThat(resultado.get(0).getFondoCategoria()).isEqualTo("FPV");
        }

        @Test
        @DisplayName("Debe retornar lista vacía si no tiene suscripciones")
        void debeRetornarListaVacia() {
            when(clienteExternalPort.findByEmail("ismael@test.com"))
                    .thenReturn(Optional.of(cliente));
            when(findByClienteIdSuscripcionRepository.findByClienteId(1L))
                    .thenReturn(List.of());

            List<Suscripcion> resultado = suscripcionService.obtenerPorCliente("ismael@test.com");

            assertThat(resultado).isEmpty();
        }
    }

    @Nested
    @DisplayName("obtenerHistorial()")
    class ObtenerHistorial {

        @Test
        @DisplayName("Debe retornar historial de transacciones enriquecido")
        void debeRetornarHistorialEnriquecido() {
            Transaccion tx = Transaccion.builder()
                    .id(UUID.randomUUID())
                    .clienteId(1L)
                    .fondoId(10L)
                    .tipo("apertura")
                    .monto(75000.0)
                    .fecha(LocalDateTime.now())
                    .build();

            when(clienteExternalPort.findByEmail("ismael@test.com"))
                    .thenReturn(Optional.of(cliente));
            when(findByClienteIdTransaccionRepository.findByClienteId(1L))
                    .thenReturn(List.of(tx));
            when(fondoExternalPort.findById(10L))
                    .thenReturn(Optional.of(fondo));

            List<Transaccion> resultado = suscripcionService.obtenerHistorial("ismael@test.com");

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getTipo()).isEqualTo("apertura");
            assertThat(resultado.get(0).getFondoNombre()).isEqualTo("FPV_BTG_PACTUAL_RECAUDADORA");
        }

        @Test
        @DisplayName("Debe lanzar excepción si cliente no existe")
        void debeLanzarExcepcionSiClienteNoExiste() {
            when(clienteExternalPort.findByEmail("noexiste@test.com"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> suscripcionService.obtenerHistorial("noexiste@test.com"))
                    .isInstanceOf(ClienteNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("obtenerPorId()")
    class ObtenerPorId {

        @Test
        @DisplayName("Debe retornar suscripción cuando existe")
        void debeRetornarSuscripcionCuandoExiste() {
            Suscripcion sus = Suscripcion.builder()
                    .id(1L).clienteId(1L).fondoId(10L).monto(75000.0).build();

            when(findByIdSuscripcionRepository.findById(1L))
                    .thenReturn(Optional.of(sus));

            Suscripcion resultado = suscripcionService.obtenerPorId(1L);

            assertThat(resultado.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Debe lanzar excepción si no existe")
        void debeLanzarExcepcionSiNoExiste() {
            when(findByIdSuscripcionRepository.findById(99L))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> suscripcionService.obtenerPorId(99L))
                    .isInstanceOf(SuscripcionNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("obtenerTodas()")
    class ObtenerTodas {

        @Test
        @DisplayName("Debe retornar todas las suscripciones")
        void debeRetornarTodas() {
            Suscripcion s1 = Suscripcion.builder().id(1L).build();
            Suscripcion s2 = Suscripcion.builder().id(2L).build();

            when(findAllSuscripcionRepository.findAll())
                    .thenReturn(List.of(s1, s2));

            List<Suscripcion> resultado = suscripcionService.obtenerTodas();

            assertThat(resultado).hasSize(2);
        }
    }
}

