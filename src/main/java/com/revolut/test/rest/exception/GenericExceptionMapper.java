package com.revolut.test.rest.exception;

import com.revolut.test.services.exception.AccountException;
import com.revolut.test.services.exception.ErrorCode;
import com.revolut.test.services.exception.ErrorMessage;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Slf4j
public class GenericExceptionMapper implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception ex) {
        Error error = createError(ex);

        if (error.getStatusCode() == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
            log.error("Application error: {}", error.getMessage(), ex);
        } else {
            log.warn("Application exception: {}", error.getMessage());
            log.debug("Exception info: ", ex);
        }

        return Response
                .status(error.getStatusCode())
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }

    private Error createError(Throwable exception) {
        ErrorMessage errorMessage = getErrorMessage(exception);


        Response.Status type = ErrorStatuses.getStatusByError(errorMessage.getServiceError());
        return Error.builder()
                .code(errorMessage.getServiceError().getCode())
                .message(getMessage(errorMessage))
                .statusCode(type.getStatusCode())
                .statusDescription(type.getReasonPhrase())
                .build();
    }

    private String getMessage(ErrorMessage errorMessage) {
        return errorMessage.toString();
    }

    private ErrorMessage getErrorMessage(Throwable exception) {
        if (exception instanceof AccountException) {
            return ((AccountException) exception).getErrorMessage();
        }
        return new ErrorMessage(ErrorCode.INTERNAL_ERROR, exception.getMessage());
    }

}
