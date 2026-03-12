package com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.in.web;

import com.btgtechnicaltest.demo.suscripcion.application.SuscripcionService;
import com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.in.web.dto.SuscripcionRequest;
import com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.in.web.dto.SuscripcionResponse;
import com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.in.web.dto.TransaccionResponse;
import com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.in.web.mapper.SuscripcionMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suscripciones")
@RequiredArgsConstructor
@Tag(name = "Suscripciones", description = "Gestión de suscripciones a fondos de inversión")
@SecurityRequirement(name = "Bearer Authentication")
public class SuscripcionController {

    private final SuscripcionService suscripcionService;
    private final SuscripcionMapper suscripcionMapper;

    @Operation(summary = "Suscribirse a un fondo", description = "Suscribe al cliente autenticado a un fondo, cobrando el monto mínimo del fondo")
    @PostMapping
    public ResponseEntity<SuscripcionResponse> suscribir(
            @RequestBody SuscripcionRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                suscripcionMapper.toResponse(suscripcionService.suscribir(
                        authentication.getName(), request.getFondoId())
                ));
    }

    @Operation(summary = "Cancelar suscripción", description = "Cancela una suscripción y devuelve el monto al saldo del cliente")
    @DeleteMapping("/{id}")
    public ResponseEntity<Double> cancelar(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok().body(suscripcionService.cancelar(authentication.getName(), id));
    }

    @Operation(summary = "Mis suscripciones", description = "Lista las suscripciones activas del cliente autenticado")
    @GetMapping("/mis-suscripciones")
    public ResponseEntity<List<SuscripcionResponse>> misSuscripciones(Authentication authentication) {
        return ResponseEntity.ok(suscripcionService.obtenerPorCliente(authentication.getName()).stream()
                .map(suscripcionMapper::toResponse)
                .toList());
    }

    @Operation(summary = "Historial de transacciones", description = "Lista todas las transacciones (aperturas y cancelaciones) del cliente autenticado")
    @GetMapping("/historial")
    public ResponseEntity<List<TransaccionResponse>> historial(Authentication authentication) {
        return ResponseEntity.ok(suscripcionService.obtenerHistorial(authentication.getName()).stream()
                .map(suscripcionMapper::toTransaccionResponse)
                .toList());
    }

    @Operation(summary = "Obtener suscripción por ID")
    @GetMapping("/{id}")
    public ResponseEntity<SuscripcionResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(suscripcionMapper.toResponse(suscripcionService.obtenerPorId(id)));
    }

    @Operation(summary = "Listar todas las suscripciones")
    @GetMapping
    public ResponseEntity<List<SuscripcionResponse>> obtenerTodas() {
        List<SuscripcionResponse> respuesta = suscripcionService.obtenerTodas().stream()
                .map(suscripcionMapper::toResponse)
                .toList();
        return ResponseEntity.ok(respuesta);
    }
}

