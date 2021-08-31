package br.com.zup.felipe.gadelha.api.handler

import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.server.exceptions.ExceptionHandler
import io.micronaut.validation.exceptions.ConstraintExceptionHandler
import jakarta.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
@Replaces(ConstraintExceptionHandler::class)
class ConstraintViolationExceptionHandler: ExceptionHandler<ConstraintViolationException, HttpResponse<*>>{
    override fun handle(request: HttpRequest<*>, ex: ConstraintViolationException): HttpResponse<*> {
        return HttpExceptionDetails.of(ex, HttpStatus.BAD_REQUEST)
    }
}