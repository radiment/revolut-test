package com.revolut.test.exception;

public enum ErrorCode implements ServiceError {
    NOT_FOUND,
    INTERNAL_ERROR,
    INCOME_NOT_POSITIVE,
    NOT_ENOUGH_MONEY;

    @Override
    public String getCode() {
        return name();
    }
}
