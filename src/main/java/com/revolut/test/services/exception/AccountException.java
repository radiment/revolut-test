package com.revolut.test.services.exception;

import java.io.Serializable;

public class AccountException extends RuntimeException {

    private final ErrorMessage errorMessage;

    public AccountException(ServiceError serviceError, Serializable... params) {
        this.errorMessage = new ErrorMessage(serviceError, params);
    }

    public AccountException(Throwable cause, ServiceError serviceError, Serializable... params) {
        super(cause);
        this.errorMessage = new ErrorMessage(serviceError, params);
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String getMessage() {
        return errorMessage.toString();
    }
}
