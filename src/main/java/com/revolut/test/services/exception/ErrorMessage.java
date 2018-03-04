package com.revolut.test.services.exception;

import lombok.Getter;

import java.io.Serializable;
import java.util.Arrays;

@Getter
public class ErrorMessage implements Serializable {
    private final ServiceError serviceError;
    private final Serializable[] params;

    public ErrorMessage(ServiceError serviceError, Serializable... params) {
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
