package com.btgtechnicaltest.demo.suscripcion.domain.model;

import com.btgtechnicaltest.demo.suscripcion.domain.exception.SaldoInsuficienteException;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Suscripcion {
    private Long id;
    private Long clienteId;
    private Long fondoId;
    private String fondoNombre;
    private String fondoCategoria;
    private Double monto;
    private Boolean activo;

    public static Suscripcion crear(ClienteInfo cliente, FondoInfo fondo) {
        validarSaldo(fondo, cliente);

        return Suscripcion.builder()
                .clienteId(cliente.getId())
                .fondoId(fondo.getId())
                .fondoNombre(fondo.getNombre())
                .fondoCategoria(fondo.getCategoria())
                .monto(fondo.getMontoMinimo())
                .activo(true)
                .build();
    }

    private static void validarSaldo(FondoInfo fondo, ClienteInfo cliente) {
        if (cliente.getSaldo() < fondo.getMontoMinimo()) {
            throw new SaldoInsuficienteException(
                    "No tiene saldo disponible para vincularse al fondo " + fondo.getNombre()
                            + ". Saldo disponible: " + cliente.getSaldo()
                            + ", valor del fondo: " + fondo.getMontoMinimo());
        }
    }
}