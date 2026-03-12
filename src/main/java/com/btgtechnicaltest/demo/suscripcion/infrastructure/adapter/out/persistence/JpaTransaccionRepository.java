package com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.out.persistence;

import com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.out.persistence.entity.TransaccionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaTransaccionRepository extends JpaRepository<TransaccionEntity, UUID> {
    List<TransaccionEntity> findByClienteIdOrderByFechaDesc(Long clienteId);
}

