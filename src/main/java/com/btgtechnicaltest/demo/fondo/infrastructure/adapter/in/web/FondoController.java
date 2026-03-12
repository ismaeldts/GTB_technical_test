package com.btgtechnicaltest.demo.fondo.infrastructure.adapter.in.web;

import com.btgtechnicaltest.demo.fondo.application.FondoService;
import com.btgtechnicaltest.demo.fondo.domain.model.Fondo;
import com.btgtechnicaltest.demo.fondo.domain.model.Moneda;
import com.btgtechnicaltest.demo.fondo.infrastructure.adapter.in.web.dto.FondoRequest;
import com.btgtechnicaltest.demo.fondo.infrastructure.adapter.in.web.dto.FondoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fondos")
@RequiredArgsConstructor
@Tag(name = "Fondos", description = "CRUD de fondos de inversión")
public class FondoController {

    private final FondoService fondoService;

    @Operation(summary = "Crear fondo")
    @PostMapping
    public ResponseEntity<FondoResponse> crear(@RequestBody FondoRequest request) {
        Fondo fondo = toDomain(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(fondoService.crear(fondo)));
    }

    @Operation(summary = "Obtener fondo por ID")
    @GetMapping("/{id}")
    public ResponseEntity<FondoResponse> obtenerPorId(@PathVariable Long id) {
        return fondoService.obtenerPorId(id)
                .map(fondo -> ResponseEntity.ok(toResponse(fondo)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Listar todos los fondos")
    @GetMapping
    public ResponseEntity<List<FondoResponse>> obtenerTodos() {
        List<FondoResponse> respuesta = fondoService.obtenerTodos().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(respuesta);
    }

    @Operation(summary = "Actualizar fondo")
    @PutMapping("/{id}")
    public ResponseEntity<FondoResponse> actualizar(@PathVariable Long id, @RequestBody FondoRequest request) {
        Fondo fondo = toDomain(request);
        fondo.setId(id);
        return ResponseEntity.ok(toResponse(fondoService.actualizar(fondo)));
    }

    @Operation(summary = "Eliminar fondo")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        fondoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private Fondo toDomain(FondoRequest request) {
        return Fondo.builder()
                .nombre(request.getNombre())
                .montoMinimo(request.getMontoMinimo())
                .moneda(Moneda.valueOf(request.getMoneda()))
                .categoria(request.getCategoria())
                .build();
    }

    private FondoResponse toResponse(Fondo fondo) {
        return FondoResponse.builder()
                .id(fondo.getId())
                .nombre(fondo.getNombre())
                .montoMinimo(fondo.getMontoMinimo())
                .moneda(fondo.getMoneda().name())
                .categoria(fondo.getCategoria())
                .build();
    }
}
