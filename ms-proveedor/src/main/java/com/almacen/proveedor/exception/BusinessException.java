package com.almacen.proveedor.exception;

/** Violacion de una regla de negocio (HTTP 409 Conflict). */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
