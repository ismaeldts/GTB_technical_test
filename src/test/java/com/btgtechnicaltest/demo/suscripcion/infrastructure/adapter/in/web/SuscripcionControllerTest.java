package com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.in.web;

import com.btgtechnicaltest.demo.shared.infrastructure.config.security.TestSecurityConfig;
import com.btgtechnicaltest.demo.suscripcion.application.SuscripcionService;
import com.btgtechnicaltest.demo.suscripcion.domain.exception.*;
import com.btgtechnicaltest.demo.suscripcion.domain.model.Suscripcion;
import com.btgtechnicaltest.demo.suscripcion.domain.model.Transaccion;
import com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.in.web.mapper.SuscripcionMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SuscripcionController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {
                        com.btgtechnicaltest.demo.shared.infrastructure.config.security.SecurityConfig.class,
                        com.btgtechnicaltest.demo.shared.infrastructure.config.security.JwtAuthenticationFilter.class
                }))
@AutoConfigureMockMvc
@Import({SuscripcionMapper.class, TestSecurityConfig.class})
class SuscripcionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SuscripcionService suscripcionService;

    private Suscripcion suscripcion;

    @BeforeEach
    void setUp() {
        suscripcion = Suscripcion.builder()
                .id(1L)
                .clienteId(1L)
                .fondoId(10L)
                .fondoNombre("FPV_BTG_PACTUAL_RECAUDADORA")
                .fondoCategoria("FPV")
                .monto(75000.0)
                .activo(true)
                .build();
    }

    @Nested
    @DisplayName("POST /api/suscripciones")
    class Suscribir {

        @Test
        @WithMockUser(username = "ismael@test.com")
        @DisplayName("Debe suscribir y retornar 201 con datos del fondo")
        void debeSuscribirExitosamente() throws Exception {
            when(suscripcionService.suscribir("ismael@test.com", 10L))
                    .thenReturn(suscripcion);

            String body = """
                    { "fondoId": 10 }
                    """;

            mockMvc.perform(post("/api/suscripciones")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.clienteId").value(1))
                    .andExpect(jsonPath("$.fondoId").value(10))
                    .andExpect(jsonPath("$.fondoNombre").value("FPV_BTG_PACTUAL_RECAUDADORA"))
                    .andExpect(jsonPath("$.fondoCategoria").value("FPV"))
                    .andExpect(jsonPath("$.monto").value(75000.0));
        }

        @Test
        @WithMockUser(username = "ismael@test.com")
        @DisplayName("Debe retornar 400 si saldo insuficiente")
        void debeRetornar400SiSaldoInsuficiente() throws Exception {
            when(suscripcionService.suscribir("ismael@test.com", 4L))
                    .thenThrow(new SaldoInsuficienteException(
                            "No tiene el saldo disponible para vincularse al fondo FDO_ACCIONES"));

            String body = """
                    { "fondoId": 4 }
                    """;

            mockMvc.perform(post("/api/suscripciones")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(
                            "No tiene el saldo disponible para vincularse al fondo FDO_ACCIONES"));
        }

        @Test
        @WithMockUser(username = "ismael@test.com")
        @DisplayName("Debe retornar 409 si suscripción duplicada")
        void debeRetornar409SiDuplicada() throws Exception {
            when(suscripcionService.suscribir("ismael@test.com", 10L))
                    .thenThrow(new SuscripcionDuplicadaException(
                            "Ya se encuentra vinculado al fondo FPV_BTG_PACTUAL_RECAUDADORA"));

            String body = """
                    { "fondoId": 10 }
                    """;

            mockMvc.perform(post("/api/suscripciones")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isConflict());
        }

        @Test
        @WithMockUser(username = "noexiste@test.com")
        @DisplayName("Debe retornar 404 si cliente no encontrado")
        void debeRetornar404SiClienteNoExiste() throws Exception {
            when(suscripcionService.suscribir("noexiste@test.com", 10L))
                    .thenThrow(new ClienteNotFoundException("Cliente no encontrado con email: noexiste@test.com"));

            String body = """
                    { "fondoId": 10 }
                    """;

            mockMvc.perform(post("/api/suscripciones")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(username = "ismael@test.com")
        @DisplayName("Debe retornar 404 si fondo no encontrado")
        void debeRetornar404SiFondoNoExiste() throws Exception {
            when(suscripcionService.suscribir("ismael@test.com", 99L))
                    .thenThrow(new FondoNotFoundException("Fondo no encontrado con id: 99"));

            String body = """
                    { "fondoId": 99 }
                    """;

            mockMvc.perform(post("/api/suscripciones")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/suscripciones/{id}")
    class Cancelar {

        @Test
        @WithMockUser(username = "ismael@test.com")
        @DisplayName("Debe cancelar y retornar monto devuelto")
        void debeCancelarExitosamente() throws Exception {
            when(suscripcionService.cancelar("ismael@test.com", 1L))
                    .thenReturn(75000.0);

            mockMvc.perform(delete("/api/suscripciones/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("75000.0"));
        }

        @Test
        @WithMockUser(username = "noexiste@test.com")
        @DisplayName("Debe retornar 404 si cliente no encontrado")
        void debeRetornar404SiClienteNoExiste() throws Exception {
            when(suscripcionService.cancelar("noexiste@test.com", 1L))
                    .thenThrow(new ClienteNotFoundException("Cliente no encontrado"));

            mockMvc.perform(delete("/api/suscripciones/1"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/suscripciones/mis-suscripciones")
    class MisSuscripciones {

        @Test
        @WithMockUser(username = "ismael@test.com")
        @DisplayName("Debe retornar suscripciones del cliente")
        void debeRetornarMisSuscripciones() throws Exception {
            when(suscripcionService.obtenerPorCliente("ismael@test.com"))
                    .thenReturn(List.of(suscripcion));

            mockMvc.perform(get("/api/suscripciones/mis-suscripciones"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].fondoNombre").value("FPV_BTG_PACTUAL_RECAUDADORA"))
                    .andExpect(jsonPath("$[0].monto").value(75000.0));
        }

        @Test
        @WithMockUser(username = "ismael@test.com")
        @DisplayName("Debe retornar lista vacía si no tiene suscripciones")
        void debeRetornarListaVacia() throws Exception {
            when(suscripcionService.obtenerPorCliente("ismael@test.com"))
                    .thenReturn(List.of());

            mockMvc.perform(get("/api/suscripciones/mis-suscripciones"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /api/suscripciones/historial")
    class Historial {

        @Test
        @WithMockUser(username = "ismael@test.com")
        @DisplayName("Debe retornar historial de transacciones")
        void debeRetornarHistorial() throws Exception {
            Transaccion tx = Transaccion.builder()
                    .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
                    .clienteId(1L)
                    .fondoId(10L)
                    .tipo("apertura")
                    .monto(75000.0)
                    .fecha(LocalDateTime.of(2026, 3, 12, 10, 0, 0))
                    .fondoNombre("FPV_BTG_PACTUAL_RECAUDADORA")
                    .fondoCategoria("FPV")
                    .build();

            when(suscripcionService.obtenerHistorial("ismael@test.com"))
                    .thenReturn(List.of(tx));

            mockMvc.perform(get("/api/suscripciones/historial"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id").value("550e8400-e29b-41d4-a716-446655440000"))
                    .andExpect(jsonPath("$[0].tipo").value("apertura"))
                    .andExpect(jsonPath("$[0].fondoNombre").value("FPV_BTG_PACTUAL_RECAUDADORA"))
                    .andExpect(jsonPath("$[0].monto").value(75000.0));
        }

        @Test
        @WithMockUser(username = "ismael@test.com")
        @DisplayName("Debe retornar historial vacío")
        void debeRetornarHistorialVacio() throws Exception {
            when(suscripcionService.obtenerHistorial("ismael@test.com"))
                    .thenReturn(List.of());

            mockMvc.perform(get("/api/suscripciones/historial"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /api/suscripciones/{id}")
    class ObtenerPorId {

        @Test
        @WithMockUser(username = "ismael@test.com")
        @DisplayName("Debe retornar suscripción por ID")
        void debeRetornarSuscripcion() throws Exception {
            when(suscripcionService.obtenerPorId(1L)).thenReturn(suscripcion);

            mockMvc.perform(get("/api/suscripciones/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.fondoNombre").value("FPV_BTG_PACTUAL_RECAUDADORA"));
        }

        @Test
        @WithMockUser(username = "ismael@test.com")
        @DisplayName("Debe retornar error si no existe")
        void debeRetornarErrorSiNoExiste() throws Exception {
            when(suscripcionService.obtenerPorId(99L))
                    .thenThrow(new SuscripcionNotFoundException("Suscripción no encontrada con id: 99"));

            mockMvc.perform(get("/api/suscripciones/99"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/suscripciones")
    class ObtenerTodas {

        @Test
        @WithMockUser(username = "ismael@test.com")
        @DisplayName("Debe retornar todas las suscripciones")
        void debeRetornarTodas() throws Exception {
            Suscripcion s2 = Suscripcion.builder()
                    .id(2L).clienteId(2L).fondoId(3L)
                    .fondoNombre("DEUDAPRIVADA").fondoCategoria("FIC")
                    .monto(50000.0).activo(true).build();

            when(suscripcionService.obtenerTodas()).thenReturn(List.of(suscripcion, s2));

            mockMvc.perform(get("/api/suscripciones"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].fondoNombre").value("FPV_BTG_PACTUAL_RECAUDADORA"))
                    .andExpect(jsonPath("$[1].fondoNombre").value("DEUDAPRIVADA"));
        }
    }
}

