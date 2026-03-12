package com.btgtechnicaltest.demo.cliente.infrastructure.adapter.in.web;

import com.btgtechnicaltest.demo.cliente.application.ClienteService;
import com.btgtechnicaltest.demo.cliente.domain.exception.ClienteAlreadyExistsException;
import com.btgtechnicaltest.demo.cliente.domain.model.Cliente;
import com.btgtechnicaltest.demo.shared.infrastructure.config.security.TestSecurityConfig;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ClienteController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {
                        com.btgtechnicaltest.demo.shared.infrastructure.config.security.SecurityConfig.class,
                        com.btgtechnicaltest.demo.shared.infrastructure.config.security.JwtAuthenticationFilter.class
                }))
@AutoConfigureMockMvc(addFilters = false)
@Import(TestSecurityConfig.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
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
    @DisplayName("POST /api/clientes")
    class Crear {

        @Test
        @DisplayName("Debe crear cliente y retornar 201")
        void debeCrearCliente() throws Exception {
            when(clienteService.crear(any(Cliente.class))).thenReturn(cliente);

            String body = """
                    {
                        "nombre": "Ismael",
                        "email": "ismael@test.com",
                        "apellidos": "Trocha",
                        "ciudad": "Bogotá"
                    }
                    """;

            mockMvc.perform(post("/api/clientes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nombre").value("Ismael"))
                    .andExpect(jsonPath("$.email").value("ismael@test.com"))
                    .andExpect(jsonPath("$.apellidos").value("Trocha"))
                    .andExpect(jsonPath("$.ciudad").value("Bogotá"));
        }

        @Test
        @DisplayName("Debe retornar 409 si email duplicado")
        void debeRetornar409SiEmailDuplicado() throws Exception {
            when(clienteService.crear(any(Cliente.class)))
                    .thenThrow(new ClienteAlreadyExistsException("Ya existe un cliente con el email: ismael@test.com"));

            String body = """
                    {
                        "nombre": "Ismael",
                        "email": "ismael@test.com",
                        "apellidos": "Trocha",
                        "ciudad": "Bogotá"
                    }
                    """;

            mockMvc.perform(post("/api/clientes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("GET /api/clientes/{id}")
    class ObtenerPorId {

        @Test
        @DisplayName("Debe retornar cliente cuando existe")
        void debeRetornarCliente() throws Exception {
            when(clienteService.obtenerPorId(1L)).thenReturn(Optional.of(cliente));

            mockMvc.perform(get("/api/clientes/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.email").value("ismael@test.com"));
        }

        @Test
        @DisplayName("Debe retornar 404 cuando no existe")
        void debeRetornar404() throws Exception {
            when(clienteService.obtenerPorId(99L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/clientes/99"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PATCH /api/clientes/saldo")
    class RecargarSaldo {

        private final Authentication authentication =
                new UsernamePasswordAuthenticationToken("ismael@test.com", null, List.of());

        @Test
        @DisplayName("Debe recargar saldo y retornar respuesta con saldo actualizado")
        void debeRecargarSaldo() throws Exception {
            Cliente clienteRecargado = Cliente.builder()
                    .id(1L).email("ismael@test.com").saldo(600000.0).build();

            when(clienteService.recargarSaldo("ismael@test.com", 100000.0))
                    .thenReturn(clienteRecargado);

            String body = """
                    { "monto": 100000.0 }
                    """;

            mockMvc.perform(patch("/api/clientes/saldo")
                            .principal(authentication)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Saldo recargado exitosamente"))
                    .andExpect(jsonPath("$.email").value("ismael@test.com"))
                    .andExpect(jsonPath("$.saldoActual").value(600000.0));
        }

        @Test
        @DisplayName("Debe retornar error si monto es negativo")
        void debeRetornarErrorSiMontoNegativo() throws Exception {
            when(clienteService.recargarSaldo(eq("ismael@test.com"), eq(-100.0)))
                    .thenThrow(new RuntimeException("El monto a recargar debe ser mayor a 0"));

            String body = """
                    { "monto": -100.0 }
                    """;

            mockMvc.perform(patch("/api/clientes/saldo")
                            .principal(authentication)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isNotFound());
        }
    }
}

