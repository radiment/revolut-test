package com.revolut.test.exception;

public class AccountException extends RuntimeException {

    private final ErrorMessage errorMessage;

    public AccountException(ServiceError serviceError, Object... params) {
        this.errorMessage = new ErrorMessage(serviceError, params);
    }

    public AccountException(Throwable cause, ServiceError serviceError, Object... params) {
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
