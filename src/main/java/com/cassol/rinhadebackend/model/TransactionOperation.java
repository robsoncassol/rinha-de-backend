package com.cassol.rinhadebackend.model;

public enum TransactionOperation {

    CREDIT("C"),
    DEBIT("D");

    private String operation;

    TransactionOperation(String operation) {
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }
}
