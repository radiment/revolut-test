package com.revolut.test.exception;

import lombok.Getter;

import java.util.Arrays;

@Getter
public class ErrorMessage {
    private final ServiceError serviceError;
    private final Object[] params;

    public ErrorMessage(ServiceError serviceError, Object... params) {
        this.serviceError = serviceError;
        this.params = params;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append("Error: ").append(serviceError.getCode());

        if (params.length > 0) {
            sb.append(" with params \"").append(Arrays.toString(params)).append('\"');
        }

        return sb.toString();
    }
}
