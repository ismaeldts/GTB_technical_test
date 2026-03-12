package com.btgtechnicaltest.demo.cliente.application;

import com.btgtechnicaltest.demo.cliente.domain.exception.ClienteAlreadyExistsException;
import com.btgtechnicaltest.demo.cliente.domain.model.Cliente;
import com.btgtechnicaltest.demo.cliente.domain.ports.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final SaveClienteRepository saveClienteRepository;
    private final FindByIdClienteRepository findByIdClienteRepository;
    private final FindAllClienteRepository findAllClienteRepository;
    private final DeleteByIdClienteRepository deleteByIdClienteRepository;
    private final FindByEmailClienteRepository findByEmailClienteRepository;

    public Cliente crear(Cliente cliente) {
        findByEmailClienteRepository.findByEmail(cliente.getEmail())
                .ifPresent(c -> {
                    throw new ClienteAlreadyExistsException("Ya existe un cliente con el email: " + cliente.getEmail());
                });
        cliente.setSaldo(500000.0);
        return saveClienteRepository.save(cliente);
    }

    public Optional<Cliente> obtenerPorId(Long id) {
        return findByIdClienteRepository.findById(id);
    }

    public List<Cliente> obtenerTodos() {
        return findAllClienteRepository.findAll();
    }

    public Cliente actualizar(Cliente cliente) {
        findByIdClienteRepository.findById(cliente.getId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + cliente.getId()));

        findByEmailClienteRepository.findByEmail(cliente.getEmail())
                .filter(existente -> !existente.getId().equals(cliente.getId()))
                .ifPresent(c -> {
                    throw new ClienteAlreadyExistsException("Ya existe otro cliente con el email: " + cliente.getEmail());
                });

        return saveClienteRepository.save(cliente);
    }

    public void eliminar(Long id) {
        findByIdClienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + id));
        deleteByIdClienteRepository.deleteById(id);
    }

    public Cliente recargarSaldo(String email, Double monto) {
        Cliente cliente = findByEmailClienteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con email: " + email));

        if (monto <= 0) {
            throw new RuntimeException("El monto a recargar debe ser mayor a 0");
        }

        cliente.setSaldo(cliente.getSaldo() + monto);
        return saveClienteRepository.save(cliente);
    }
}
