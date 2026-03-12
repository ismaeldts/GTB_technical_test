package com.btgtechnicaltest.demo.fondo.application;

import com.btgtechnicaltest.demo.fondo.domain.exception.FondoAlreadyExistsException;
import com.btgtechnicaltest.demo.fondo.domain.model.Fondo;
import com.btgtechnicaltest.demo.fondo.domain.ports.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FondoService {

    private final SaveFondoRepository saveFondoRepository;
    private final FindByIdFondoRepository findByIdFondoRepository;
    private final FindAllFondoRepository findAllFondoRepository;
    private final DeleteByIdFondoRepository deleteByIdFondoRepository;
    private final FindByNombreFondoRepository findByNombreFondoRepository;

    public Fondo crear(Fondo fondo) {
        findByNombreFondoRepository.findByNombre(fondo.getNombre())
                .ifPresent(f -> {
                    throw new FondoAlreadyExistsException("Ya existe un fondo con el nombre: " + fondo.getNombre());
                });

        return saveFondoRepository.save(fondo);
    }

    public Optional<Fondo> obtenerPorId(Long id) {
        return findByIdFondoRepository.findById(id);
    }

    public List<Fondo> obtenerTodos() {
        return findAllFondoRepository.findAll();
    }

    public Fondo actualizar(Fondo fondo) {
        findByIdFondoRepository.findById(fondo.getId())
                .orElseThrow(() -> new RuntimeException("Fondo no encontrado con id: " + fondo.getId()));

        findByNombreFondoRepository.findByNombre(fondo.getNombre())
                .filter(existente -> !existente.getId().equals(fondo.getId()))
                .ifPresent(f -> {
                    throw new FondoAlreadyExistsException("Ya existe otro fondo con el nombre: " + fondo.getNombre());
                });

        return saveFondoRepository.save(fondo);
    }

    public void eliminar(Long id) {
        findByIdFondoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fondo no encontrado con id: " + id));
        deleteByIdFondoRepository.deleteById(id);
    }
}
