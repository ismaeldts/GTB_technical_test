package com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.out.persistence.repository;

import com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.out.persistence.entity.SuscripcionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaSuscripcionRepository extends JpaRepository<SuscripcionEntity, Long> {
    List<SuscripcionEntity> findByClienteIdAndActivoTrue(Long clienteId);

    Optional<SuscripcionEntity> findByClienteIdAndFondoIdAndActivoTrue(Long clienteId, Long fondoId);

    List<SuscripcionEntity> findByActivoTrue();

    Optional<SuscripcionEntity> findByIdAndActivoTrue(Long id);
}

