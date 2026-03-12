package com.btgtechnicaltest.demo.suscripcion.infrastructure.adapter.out.external;

import com.btgtechnicaltest.demo.fondo.domain.ports.FindByIdFondoRepository;
import com.btgtechnicaltest.demo.suscripcion.domain.model.FondoInfo;
import com.btgtechnicaltest.demo.suscripcion.domain.ports.FondoExternalPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FondoModuleAdapter implements FondoExternalPort {

    private final FindByIdFondoRepository findByIdFondoRepository;

    @Override
    public Optional<FondoInfo> findById(Long fondoId) {
        return findByIdFondoRepository.findById(fondoId)
                .map(fondo -> FondoInfo.builder()
                        .id(fondo.getId())
                        .nombre(fondo.getNombre())
                        .montoMinimo(fondo.getMontoMinimo())
                        .categoria(fondo.getCategoria())
                        .build());
    }
}

