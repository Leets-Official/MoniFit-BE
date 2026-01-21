package com.leets.monifit_be.global.exception;

public class ActiveBudgetNotFoundException extends RuntimeException {
    public ActiveBudgetNotFoundException(String message) {
        super(message);
    }
}
