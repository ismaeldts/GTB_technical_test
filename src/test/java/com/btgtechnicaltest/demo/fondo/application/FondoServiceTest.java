package com.btgtechnicaltest.demo.fondo.application;

import com.btgtechnicaltest.demo.fondo.domain.exception.FondoAlreadyExistsException;
import com.btgtechnicaltest.demo.fondo.domain.model.Fondo;
import com.btgtechnicaltest.demo.fondo.domain.model.Moneda;
import com.btgtechnicaltest.demo.fondo.domain.ports.*;
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
class FondoServiceTest {

    @Mock
    private SaveFondoRepository saveFondoRepository;
    @Mock
    private FindByIdFondoRepository findByIdFondoRepository;
    @Mock
    private FindAllFondoRepository findAllFondoRepository;
    @Mock
    private DeleteByIdFondoRepository deleteByIdFondoRepository;
    @Mock
    private FindByNombreFondoRepository findByNombreFondoRepository;

    @InjectMocks
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
    @DisplayName("crear()")
    class Crear {

        @Test
        @DisplayName("Debe crear fondo cuando el nombre no existe")
        void debeCrearFondoExitosamente() {
            when(findByNombreFondoRepository.findByNombre(fondo.getNombre()))
                    .thenReturn(Optional.empty());
            when(saveFondoRepository.save(any(Fondo.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            Fondo resultado = fondoService.crear(fondo);

            assertThat(resultado.getNombre()).isEqualTo("FPV_BTG_PACTUAL_RECAUDADORA");
            assertThat(resultado.getMontoMinimo()).isEqualTo(75000.0);
            verify(saveFondoRepository).save(fondo);
        }

        @Test
        @DisplayName("Debe lanzar excepción si ya existe un fondo con el mismo nombre")
        void debeLanzarExcepcionSiNombreDuplicado() {
            when(findByNombreFondoRepository.findByNombre(fondo.getNombre()))
                    .thenReturn(Optional.of(fondo));

            assertThatThrownBy(() -> fondoService.crear(fondo))
                    .isInstanceOf(FondoAlreadyExistsException.class)
                    .hasMessageContaining(fondo.getNombre());

            verify(saveFondoRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("obtenerPorId()")
    class ObtenerPorId {

        @Test
        @DisplayName("Debe retornar fondo cuando existe")
        void debeRetornarFondoCuandoExiste() {
            when(findByIdFondoRepository.findById(1L))
                    .thenReturn(Optional.of(fondo));

            Optional<Fondo> resultado = fondoService.obtenerPorId(1L);

            assertThat(resultado).isPresent();
            assertThat(resultado.get().getNombre()).isEqualTo("FPV_BTG_PACTUAL_RECAUDADORA");
        }

        @Test
        @DisplayName("Debe retornar vacío cuando no existe")
        void debeRetornarVacioCuandoNoExiste() {
            when(findByIdFondoRepository.findById(99L))
                    .thenReturn(Optional.empty());

            Optional<Fondo> resultado = fondoService.obtenerPorId(99L);

            assertThat(resultado).isEmpty();
        }
    }

    @Nested
    @DisplayName("obtenerTodos()")
    class ObtenerTodos {

        @Test
        @DisplayName("Debe retornar lista de fondos")
        void debeRetornarListaDeFondos() {
            Fondo fondo2 = Fondo.builder()
                    .id(2L)
                    .nombre("DEUDAPRIVADA")
                    .montoMinimo(50000.0)
                    .moneda(Moneda.COP)
                    .categoria("FIC")
                    .build();

            when(findAllFondoRepository.findAll())
                    .thenReturn(List.of(fondo, fondo2));

            List<Fondo> resultado = fondoService.obtenerTodos();

            assertThat(resultado).hasSize(2);
        }
    }

    @Nested
    @DisplayName("actualizar()")
    class Actualizar {

        @Test
        @DisplayName("Debe actualizar fondo existente")
        void debeActualizarFondoExistente() {
            when(findByIdFondoRepository.findById(1L))
                    .thenReturn(Optional.of(fondo));
            when(findByNombreFondoRepository.findByNombre(fondo.getNombre()))
                    .thenReturn(Optional.of(fondo));
            when(saveFondoRepository.save(any(Fondo.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            fondo.setMontoMinimo(80000.0);
            Fondo resultado = fondoService.actualizar(fondo);

            assertThat(resultado.getMontoMinimo()).isEqualTo(80000.0);
            verify(saveFondoRepository).save(fondo);
        }

        @Test
        @DisplayName("Debe lanzar excepción si fondo no existe")
        void debeLanzarExcepcionSiFondoNoExiste() {
            when(findByIdFondoRepository.findById(99L))
                    .thenReturn(Optional.empty());

            fondo.setId(99L);

            assertThatThrownBy(() -> fondoService.actualizar(fondo))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Fondo no encontrado");
        }

        @Test
        @DisplayName("Debe lanzar excepción si nombre ya pertenece a otro fondo")
        void debeLanzarExcepcionSiNombreDeOtroFondo() {
            Fondo otroFondo = Fondo.builder().id(2L).nombre("FPV_BTG_PACTUAL_RECAUDADORA").build();

            when(findByIdFondoRepository.findById(1L))
                    .thenReturn(Optional.of(fondo));
            when(findByNombreFondoRepository.findByNombre(fondo.getNombre()))
                    .thenReturn(Optional.of(otroFondo));

            assertThatThrownBy(() -> fondoService.actualizar(fondo))
                    .isInstanceOf(FondoAlreadyExistsException.class)
                    .hasMessageContaining("Ya existe otro fondo");
        }
    }

    @Nested
    @DisplayName("eliminar()")
    class Eliminar {

        @Test
        @DisplayName("Debe eliminar fondo existente")
        void debeEliminarFondoExistente() {
            when(findByIdFondoRepository.findById(1L))
                    .thenReturn(Optional.of(fondo));

            fondoService.eliminar(1L);

            verify(deleteByIdFondoRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Debe lanzar excepción si fondo no existe")
        void debeLanzarExcepcionSiFondoNoExiste() {
            when(findByIdFondoRepository.findById(99L))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> fondoService.eliminar(99L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Fondo no encontrado");
        }
    }
}

