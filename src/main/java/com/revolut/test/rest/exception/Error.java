package com.revolut.test.rest.exception;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Error {
    private String code;
    private String message;
    private int statusCode;
    private String statusDescription;
}
