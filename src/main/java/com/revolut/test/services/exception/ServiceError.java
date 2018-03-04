package com.revolut.test.services.exception;

import java.io.Serializable;

public interface ServiceError extends Serializable {
    String getCode();
}
