package com.cassol.rinhadebackend.exceptions;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(Class clazz, Long id) {
        super("Entity " + clazz.getSimpleName() + " with id " + id + " not found.");
    }
}
