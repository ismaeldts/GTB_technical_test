package com.btgtechnicaltest.demo.cliente.infrastructure.adapter.in.web;

import com.btgtechnicaltest.demo.cliente.application.ClienteService;
import com.btgtechnicaltest.demo.cliente.domain.model.Cliente;
import com.btgtechnicaltest.demo.cliente.infrastructure.adapter.in.web.dto.ClienteRequest;
import com.btgtechnicaltest.demo.cliente.infrastructure.adapter.in.web.dto.ClienteResponse;
import com.btgtechnicaltest.demo.cliente.infrastructure.adapter.in.web.dto.RecargaSaldoRequest;
import com.btgtechnicaltest.demo.cliente.infrastructure.adapter.in.web.dto.RecargaSaldoResponse;
import com.btgtechnicaltest.demo.cliente.infrastructure.adapter.in.web.mapper.ClienteRequestMapper;
import com.btgtechnicaltest.demo.cliente.infrastructure.adapter.in.web.mapper.ClienteResponseMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "CRUD de clientes y recarga de saldo")
public class ClienteController {

    private final ClienteService clienteService;

    @Operation(summary = "Crear cliente")
    @PostMapping
    public ResponseEntity<ClienteResponse> crear(@RequestBody ClienteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ClienteResponseMapper.fromDomain(
                        clienteService.crear(ClienteRequestMapper.toDomain(request)))
                );
    }

    @Operation(summary = "Obtener cliente por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> obtenerPorId(@PathVariable Long id) {
        return clienteService.obtenerPorId(id)
                .map(ClienteResponseMapper::fromDomain)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Recargar saldo", description = "Recarga el saldo del cliente autenticado. Requiere token JWT")
    @SecurityRequirement(name = "Bearer Authentication")
    @PatchMapping("/saldo")
    public ResponseEntity<RecargaSaldoResponse> recargarSaldo(
            @RequestBody RecargaSaldoRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        Cliente cliente = clienteService.recargarSaldo(email, request.getMonto());
        return ResponseEntity.ok(RecargaSaldoResponse.builder()
                .message("Saldo recargado exitosamente")
                .email(cliente.getEmail())
                .saldoActual(cliente.getSaldo())
                .build());
    }
}
