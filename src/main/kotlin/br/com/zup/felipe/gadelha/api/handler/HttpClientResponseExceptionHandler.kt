package br.com.zup.felipe.gadelha.api.handler

import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.http.server.exceptions.ExceptionHandler
import jakarta.inject.Singleton

@Singleton
@Replaces(Exception::class)
class HttpClientResponseExceptionHandler: ExceptionHandler<Exception, HttpResponse<*>>{
    override fun handle(request: HttpRequest<*>, ex: Exception): HttpResponse<*> {
        return when (ex) {
            is HttpClientResponseException -> HttpExceptionDetails.of(ex, HttpStatus.BAD_REQUEST)
            else -> HttpExceptionDetails.of(ex, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}