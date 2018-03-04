package com.revolut.test.services.exception;

public enum ErrorCode implements ServiceError {
    NOT_FOUND,
    INTERNAL_ERROR,
    INCOME_NOT_POSITIVE,
    NOT_ENOUGH_MONEY,
    CONCURRENT_CHANGE,
    WITHDRAWAL_NOT_POSITIVE;

    @Override
    public String getCode() {
        return name();
    }
}
