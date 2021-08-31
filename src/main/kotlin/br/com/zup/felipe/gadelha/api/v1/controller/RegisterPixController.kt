package br.com.zup.felipe.gadelha.api.v1.controller

import br.com.zup.felipe.gadelha.KeyManagerRegisterServiceGrpc
import br.com.zup.felipe.gadelha.api.v1.dto.request.PixRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.Post
import io.micronaut.http.uri.UriBuilder
import io.micronaut.validation.Validated
import java.util.*
import javax.validation.Valid

@Validated
@Controller("/api/v1/pix")
class RegisterPixController(
    val grpcClient: KeyManagerRegisterServiceGrpc.KeyManagerRegisterServiceBlockingStub
) {
    @Post
    fun register(@Header("CLIENT_ID") clientId: UUID, @Body @Valid pix: PixRequest): HttpResponse<Any> {
        println(pix)
        val response = grpcClient.register(pix.convertGrpc(clientId))
        val uri = UriBuilder.of("/api/v1/pix/{pixId}")
            .expand(mutableMapOf("pixId" to response.pixId))
        return HttpResponse.created(uri)
    }
}









//    @Error(global = true)
//    fun error(request: HttpRequest<*>, e: Throwable): HttpResponse<*> {
//        return HttpExceptionDetails.of(e, HttpStatus.BAD_REQUEST)
//    }
