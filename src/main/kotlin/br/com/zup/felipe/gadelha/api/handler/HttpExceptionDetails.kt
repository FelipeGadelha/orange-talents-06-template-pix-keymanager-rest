package br.com.zup.felipe.gadelha.api.handler

import com.google.rpc.BadRequest
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import java.time.OffsetDateTime
import javax.validation.ConstraintViolationException

data class HttpExceptionDetails private constructor(
    val title: String,
    val status: Int,
    val detail: String,
    val timestamp: OffsetDateTime,
    val errors: Map<String, String>? = null,
) {
    companion object {
        fun of(exception: StatusRuntimeException, httpStatus: HttpStatus): HttpResponse<Any> {
            var errors: Map<String, String>? = null
            if (httpStatus == HttpStatus.BAD_REQUEST) {
                errors = StatusProto.fromThrowable(exception).let { status ->
                    val badRequest = status!!.detailsList[0].unpack(BadRequest::class.java)
                    badRequest.fieldViolationsList
                        .associate { list -> list.field to list.description }
                }
            }
            return HttpResponse
                    .status<Any>(httpStatus)
                    .body(HttpExceptionDetails(
                        title = httpStatus.reason,
                        status = httpStatus.code,
                        detail = exception.message.toString(),
                        timestamp = OffsetDateTime.now(),
                        errors = errors
                    )
                )
        }
        fun of(exception: ConstraintViolationException, httpStatus: HttpStatus): HttpResponse<Any> {
            val errors = exception.constraintViolations
                .associate { list -> list.invalidValue.toString() to list.message }
            return HttpResponse
                .status<Any>(httpStatus)
                .body(HttpExceptionDetails(
                    title = httpStatus.reason,
                    status = httpStatus.code,
                    detail = "Dados Inválidos",
                    timestamp = OffsetDateTime.now(),
                    errors = errors
                    )
                )
        }
        fun of(exception: Exception, httpStatus: HttpStatus): HttpResponse<Any> {
            return HttpResponse
                .status<Any>(httpStatus)
                .body(HttpExceptionDetails(
                    title = httpStatus.reason,
                    status = httpStatus.code,
                    detail = "Dados Inválidos",
                    timestamp = OffsetDateTime.now(),
                )
            )
        }
    }
}