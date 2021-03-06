package com.revolut.test.rest.exception;

import com.revolut.test.services.exception.ServiceError;

import javax.ws.rs.core.Response.Status;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.revolut.test.services.exception.ErrorCode.CONCURRENT_CHANGE;
import static com.revolut.test.services.exception.ErrorCode.INCOME_NOT_POSITIVE;
import static com.revolut.test.services.exception.ErrorCode.INTERNAL_ERROR;
import static com.revolut.test.services.exception.ErrorCode.NOT_ENOUGH_MONEY;
import static com.revolut.test.services.exception.ErrorCode.NOT_FOUND;

public class ErrorStatuses {

    private ErrorStatuses() {}

    public static final String WEB_APP_ERROR = "WEB_APP_ERROR";

    private static final Map<ServiceError, Status> statuses;
    static {
        Map<ServiceError, Status> st = new HashMap<>();
        st.put(NOT_FOUND, Status.NOT_FOUND);
        st.put(INTERNAL_ERROR, Status.INTERNAL_SERVER_ERROR);
        st.put(INCOME_NOT_POSITIVE, Status.BAD_REQUEST);
        st.put(NOT_ENOUGH_MONEY, Status.BAD_REQUEST);
        st.put(CONCURRENT_CHANGE, Status.CONFLICT);
        statuses = Collections.unmodifiableMap(st);
    }

    private static final Status DEFAULT_STATUS = Status.INTERNAL_SERVER_ERROR;

    public static Status getStatusByError(ServiceError error) {
        return statuses.getOrDefault(error, DEFAULT_STATUS);
    }
}
