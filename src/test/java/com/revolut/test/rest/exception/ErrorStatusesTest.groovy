package com.revolut.test.rest.exception

import com.revolut.test.services.exception.ErrorCode
import com.revolut.test.services.exception.ServiceError
import spock.lang.Specification

import javax.ws.rs.core.Response.Status

class ErrorStatusesTest extends Specification {
    def "unknown serviceError should return INTERNAL_SERVER_ERROR"() {
        given:
        ServiceError error = { "UNKNOWN" }

        when:
        def result = ErrorStatuses.getStatusByError(error)

        then:
        result == Status.INTERNAL_SERVER_ERROR
    }

    def "statuses for main errors"(ServiceError error, Status status) {
        expect:
        ErrorStatuses.getStatusByError(error) == status

        where:
        error                         | status
        ErrorCode.NOT_FOUND           | Status.NOT_FOUND
        ErrorCode.INTERNAL_ERROR      | Status.INTERNAL_SERVER_ERROR
        ErrorCode.INCOME_NOT_POSITIVE | Status.BAD_REQUEST

    }
}
