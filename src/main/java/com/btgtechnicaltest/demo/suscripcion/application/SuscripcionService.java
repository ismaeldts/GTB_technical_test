package com.btgtechnicaltest.demo.suscripcion.application;

import com.btgtechnicaltest.demo.suscripcion.domain.exception.ClienteNotFoundException;
import com.btgtechnicaltest.demo.suscripcion.domain.exception.FondoNotFoundException;
import com.btgtechnicaltest.demo.suscripcion.domain.exception.SuscripcionDuplicadaException;
import com.btgtechnicaltest.demo.suscripcion.domain.exception.SuscripcionNotFoundException;
import com.btgtechnicaltest.demo.suscripcion.domain.model.ClienteInfo;
import com.btgtechnicaltest.demo.suscripcion.domain.model.FondoInfo;
import com.btgtechnicaltest.demo.suscripcion.domain.model.Suscripcion;
import com.btgtechnicaltest.demo.suscripcion.domain.model.Transaccion;
import com.btgtechnicaltest.demo.suscripcion.domain.ports.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SuscripcionService {

    private final SaveSuscripcionRepository saveSuscripcionRepository;
    private final FindByIdSuscripcionRepository findByIdSuscripcionRepository;
    private final FindAllSuscripcionRepository findAllSuscripcionRepository;
    private final FindByClienteIdSuscripcionRepository findByClienteIdSuscripcionRepository;
    private final DeleteByIdSuscripcionRepository deleteByIdSuscripcionRepository;
    private final SaveTransaccionRepository saveTransaccionRepository;
    private final FindByClienteIdTransaccionRepository findByClienteIdTransaccionRepository;
    private final ClienteExternalPort clienteExternalPort;
    private final FondoExternalPort fondoExternalPort;

    @Transactional
    public Suscripcion suscribir(String emailCliente, Long fondoId) {
        ClienteInfo cliente = obtenerCliente(emailCliente);

        FondoInfo fondo = fondoExternalPort.findById(fondoId)
                .orElseThrow(() -> new FondoNotFoundException(
                        "Fondo no encontrado con id: " + fondoId));

        findByClienteIdSuscripcionRepository.findByClienteIdAndFondoId(cliente.getId(), fondoId)
                .ifPresent(s -> {
                    throw new SuscripcionDuplicadaException(
                            "Ya se encuentra vinculado al fondo " + fondo.getNombre());
                });

        Suscripcion suscripcion = Suscripcion.crear(cliente, fondo);
        clienteExternalPort.debitarSaldo(cliente.getId(), fondo.getMontoMinimo());

        Suscripcion saved = saveSuscripcionRepository.save(suscripcion);
        saved.setFondoNombre(fondo.getNombre());
        saved.setFondoCategoria(fondo.getCategoria());

        saveTransaccionRepository.save(Transaccion.apertura(cliente.getId(), fondo));

        return saved;
    }

    @Transactional
    public Double cancelar(String emailCliente, Long suscripcionId) {
        ClienteInfo cliente = obtenerCliente(emailCliente);

        Suscripcion suscripcion = findByIdSuscripcionRepository.findById(suscripcionId)
                .orElseThrow(() -> new RuntimeException("Suscripción no encontrada con id: " + suscripcionId));

        FondoInfo fondo = fondoExternalPort.findById(suscripcion.getFondoId())
                .orElseThrow(() -> new FondoNotFoundException(
                        "Fondo no encontrado con id: " + suscripcion.getFondoId()));

        clienteExternalPort.acreditarSaldo(cliente.getId(), suscripcion.getMonto());

        deleteByIdSuscripcionRepository.deleteById(suscripcionId);

        saveTransaccionRepository.save(Transaccion.cancelacion(cliente.getId(), fondo, suscripcion.getMonto()));
        return suscripcion.getMonto();
    }

    public List<Suscripcion> obtenerPorCliente(String emailCliente) {
        ClienteInfo cliente = obtenerCliente(emailCliente);

        return findByClienteIdSuscripcionRepository.findByClienteId(cliente.getId()).stream()
                .map(this::enriquecerConFondo)
                .toList();
    }

    public List<Transaccion> obtenerHistorial(String emailCliente) {
        ClienteInfo cliente = obtenerCliente(emailCliente);

        return findByClienteIdTransaccionRepository.findByClienteId(cliente.getId()).stream()
                .map(this::enriquecerTransaccionConFondo)
                .toList();
    }

    public Suscripcion obtenerPorId(Long id) {
        return findByIdSuscripcionRepository.findById(id).orElseThrow(() -> new SuscripcionNotFoundException(" Suscripción no encontrada con id: " + id));
    }

    public List<Suscripcion> obtenerTodas() {
        return findAllSuscripcionRepository.findAll();
    }

    private ClienteInfo obtenerCliente(String emailCliente) {
        return clienteExternalPort.findByEmail(emailCliente)
                .orElseThrow(() -> new ClienteNotFoundException(
                        "Cliente no encontrado con email: " + emailCliente));
    }

    private Suscripcion enriquecerConFondo(Suscripcion suscripcion) {
        fondoExternalPort.findById(suscripcion.getFondoId())
                .ifPresent(fondo -> {
                    suscripcion.setFondoNombre(fondo.getNombre());
                    suscripcion.setFondoCategoria(fondo.getCategoria());
                });
        return suscripcion;
    }

    private Transaccion enriquecerTransaccionConFondo(Transaccion transaccion) {
        fondoExternalPort.findById(transaccion.getFondoId())
                .ifPresent(fondo -> {
                    transaccion.setFondoNombre(fondo.getNombre());
                    transaccion.setFondoCategoria(fondo.getCategoria());
                });
        return transaccion;
    }
}

