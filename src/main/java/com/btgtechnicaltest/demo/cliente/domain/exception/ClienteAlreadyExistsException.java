package com.btgtechnicaltest.demo.cliente.domain.exception;

public class ClienteAlreadyExistsException extends RuntimeException {

    public ClienteAlreadyExistsException(String message) {
        super(message);
    }
}

