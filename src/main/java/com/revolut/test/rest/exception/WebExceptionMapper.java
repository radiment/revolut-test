package com.revolut.test.rest.exception;

import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Slf4j
public class WebExceptionMapper implements ExceptionMapper<WebApplicationException> {
    @Override
    public Response toResponse(WebApplicationException ex) {
        log.warn("Web application exception: {}", ex.getLocalizedMessage());
        log.debug("Exception info: ", ex);

        Response.Status status = Response.Status.fromStatusCode(ex.getResponse().getStatus());

        Error error = Error.builder()
                .code(ErrorStatuses.WEB_APP_ERROR)
                .message(ex.getLocalizedMessage())
                .statusCode(status.getStatusCode())
                .statusDescription(status.getReasonPhrase())
                .build();

        return Response
                .status(status.getStatusCode())
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
