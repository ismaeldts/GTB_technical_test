package com.btgtechnicaltest.demo.fondo.infrastructure.adapter.in.web;

import com.btgtechnicaltest.demo.fondo.application.FondoService;
import com.btgtechnicaltest.demo.fondo.domain.exception.FondoAlreadyExistsException;
import com.btgtechnicaltest.demo.fondo.domain.model.Fondo;
import com.btgtechnicaltest.demo.fondo.domain.model.Moneda;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FondoController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {
                        com.btgtechnicaltest.demo.shared.infrastructure.config.security.SecurityConfig.class,
                        com.btgtechnicaltest.demo.shared.infrastructure.config.security.JwtAuthenticationFilter.class
                }))
@AutoConfigureMockMvc(addFilters = false)
@Import(TestSecurityConfig.class)
class FondoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FondoService fondoService;

    private Fondo fondo;

    @BeforeEach
    void setUp() {
        fondo = Fondo.builder()
                .id(1L)
                .nombre("FPV_BTG_PACTUAL_RECAUDADORA")
                .montoMinimo(75000.0)
                .moneda(Moneda.COP)
                .categoria("FPV")
                .build();
    }

    @Nested
    @DisplayName("POST /api/fondos")
    class Crear {

        @Test
        @DisplayName("Debe crear fondo y retornar 201")
        void debeCrearFondo() throws Exception {
            when(fondoService.crear(any(Fondo.class))).thenReturn(fondo);

            String body = """
                    {
                        "nombre": "FPV_BTG_PACTUAL_RECAUDADORA",
                        "montoMinimo": 75000.0,
                        "moneda": "COP",
                        "categoria": "FPV"
                    }
                    """;

            mockMvc.perform(post("/api/fondos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nombre").value("FPV_BTG_PACTUAL_RECAUDADORA"))
                    .andExpect(jsonPath("$.montoMinimo").value(75000.0))
                    .andExpect(jsonPath("$.moneda").value("COP"))
                    .andExpect(jsonPath("$.categoria").value("FPV"));
        }

        @Test
        @DisplayName("Debe retornar 409 si nombre duplicado")
        void debeRetornar409SiNombreDuplicado() throws Exception {
            when(fondoService.crear(any(Fondo.class)))
                    .thenThrow(new FondoAlreadyExistsException("Ya existe un fondo con el nombre: FPV_BTG_PACTUAL_RECAUDADORA"));

            String body = """
                    {
                        "nombre": "FPV_BTG_PACTUAL_RECAUDADORA",
                        "montoMinimo": 75000.0,
                        "moneda": "COP",
                        "categoria": "FPV"
                    }
                    """;

            mockMvc.perform(post("/api/fondos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("GET /api/fondos/{id}")
    class ObtenerPorId {

        @Test
        @DisplayName("Debe retornar fondo cuando existe")
        void debeRetornarFondo() throws Exception {
            when(fondoService.obtenerPorId(1L)).thenReturn(Optional.of(fondo));

            mockMvc.perform(get("/api/fondos/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nombre").value("FPV_BTG_PACTUAL_RECAUDADORA"))
                    .andExpect(jsonPath("$.montoMinimo").value(75000.0));
        }

        @Test
        @DisplayName("Debe retornar 404 cuando no existe")
        void debeRetornar404() throws Exception {
            when(fondoService.obtenerPorId(99L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/fondos/99"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/fondos")
    class ObtenerTodos {

        @Test
        @DisplayName("Debe retornar lista de fondos")
        void debeRetornarLista() throws Exception {
            Fondo fondo2 = Fondo.builder()
                    .id(2L).nombre("DEUDAPRIVADA").montoMinimo(50000.0)
                    .moneda(Moneda.COP).categoria("FIC").build();

            when(fondoService.obtenerTodos()).thenReturn(List.of(fondo, fondo2));

            mockMvc.perform(get("/api/fondos"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].nombre").value("FPV_BTG_PACTUAL_RECAUDADORA"))
                    .andExpect(jsonPath("$[1].nombre").value("DEUDAPRIVADA"));
        }

        @Test
        @DisplayName("Debe retornar lista vacía")
        void debeRetornarListaVacia() throws Exception {
            when(fondoService.obtenerTodos()).thenReturn(List.of());

            mockMvc.perform(get("/api/fondos"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("PUT /api/fondos/{id}")
    class Actualizar {

        @Test
        @DisplayName("Debe actualizar fondo y retornar 200")
        void debeActualizarFondo() throws Exception {
            Fondo actualizado = Fondo.builder()
                    .id(1L).nombre("FPV_BTG_ACTUALIZADO").montoMinimo(80000.0)
                    .moneda(Moneda.COP).categoria("FPV").build();

            when(fondoService.actualizar(any(Fondo.class))).thenReturn(actualizado);

            String body = """
                    {
                        "nombre": "FPV_BTG_ACTUALIZADO",
                        "montoMinimo": 80000.0,
                        "moneda": "COP",
                        "categoria": "FPV"
                    }
                    """;

            mockMvc.perform(put("/api/fondos/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nombre").value("FPV_BTG_ACTUALIZADO"))
                    .andExpect(jsonPath("$.montoMinimo").value(80000.0));
        }

        @Test
        @DisplayName("Debe retornar error si fondo no existe")
        void debeRetornarErrorSiFondoNoExiste() throws Exception {
            when(fondoService.actualizar(any(Fondo.class)))
                    .thenThrow(new RuntimeException("Fondo no encontrado con id: 99"));

            String body = """
                    {
                        "nombre": "INEXISTENTE",
                        "montoMinimo": 50000.0,
                        "moneda": "COP",
                        "categoria": "FIC"
                    }
                    """;

            mockMvc.perform(put("/api/fondos/99")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/fondos/{id}")
    class Eliminar {

        @Test
        @DisplayName("Debe eliminar fondo y retornar 204")
        void debeEliminarFondo() throws Exception {
            doNothing().when(fondoService).eliminar(1L);

            mockMvc.perform(delete("/api/fondos/1"))
                    .andExpect(status().isNoContent());

            verify(fondoService).eliminar(1L);
        }

        @Test
        @DisplayName("Debe retornar error si fondo no existe")
        void debeRetornarErrorSiFondoNoExiste() throws Exception {
            doThrow(new RuntimeException("Fondo no encontrado con id: 99"))
                    .when(fondoService).eliminar(99L);

            mockMvc.perform(delete("/api/fondos/99"))
                    .andExpect(status().isNotFound());
        }
    }
}
