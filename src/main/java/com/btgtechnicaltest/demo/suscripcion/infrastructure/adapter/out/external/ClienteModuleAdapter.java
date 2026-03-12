package com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.out.external;

import com.btgtechnicaltest.demo.cliente.domain.model.Cliente;
import com.btgtechnicaltest.demo.cliente.domain.ports.FindByEmailClienteRepository;
import com.btgtechnicaltest.demo.cliente.domain.ports.FindByIdClienteRepository;
import com.btgtechnicaltest.demo.cliente.domain.ports.SaveClienteRepository;
import com.btgtechnicaltest.demo.suscripcion.domain.model.ClienteInfo;
import com.btgtechnicaltest.demo.suscripcion.domain.ports.ClienteExternalPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ClienteModuleAdapter implements ClienteExternalPort {

    private final FindByIdClienteRepository findByIdClienteRepository;
    private final FindByEmailClienteRepository findByEmailClienteRepository;
    private final SaveClienteRepository saveClienteRepository;

    @Override
    public boolean existsById(Long clienteId) {
        return findByIdClienteRepository.findById(clienteId).isPresent();
    }

    @Override
    public Optional<ClienteInfo> findByEmail(String email) {
        return findByEmailClienteRepository.findByEmail(email)
                .map(cliente -> ClienteInfo.builder()
                        .id(cliente.getId())
                        .email(cliente.getEmail())
                        .saldo(cliente.getSaldo())
                        .build());
    }

    @Override
    public void debitarSaldo(Long clienteId, Double monto) {
        Cliente cliente = findByIdClienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + clienteId));
        cliente.setSaldo(cliente.getSaldo() - monto);
        saveClienteRepository.save(cliente);
    }

    @Override
    public void acreditarSaldo(Long clienteId, Double monto) {
        Cliente cliente = findByIdClienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + clienteId));
        cliente.setSaldo(cliente.getSaldo() + monto);
        saveClienteRepository.save(cliente);
    }
}

