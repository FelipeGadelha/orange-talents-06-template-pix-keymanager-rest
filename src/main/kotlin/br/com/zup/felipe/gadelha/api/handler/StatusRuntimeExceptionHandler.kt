package br.com.zup.felipe.gadelha.api.handler

import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus.*
import io.micronaut.http.server.exceptions.ExceptionHandler
import jakarta.inject.Singleton

@Singleton
class StatusRuntimeExceptionHandler: ExceptionHandler<StatusRuntimeException, HttpResponse<*>> {

    override fun handle(request: HttpRequest<*>, ex: StatusRuntimeException): HttpResponse<*> {
        return when (ex.status.code) {
                Status.ALREADY_EXISTS.code -> HttpExceptionDetails.of(ex, UNPROCESSABLE_ENTITY)
                Status.INVALID_ARGUMENT.code -> HttpExceptionDetails.of(ex, BAD_REQUEST)
                Status.FAILED_PRECONDITION.code -> HttpExceptionDetails.of(ex, BAD_REQUEST)
                Status.NOT_FOUND.code -> HttpExceptionDetails.of(ex, NOT_FOUND)
                Status.PERMISSION_DENIED.code -> HttpExceptionDetails.of(ex, FORBIDDEN)
                Status.UNKNOWN.code -> HttpExceptionDetails.of(ex, INTERNAL_SERVER_ERROR)
                else -> HttpExceptionDetails.of(ex, INTERNAL_SERVER_ERROR)
            }
    }
}


