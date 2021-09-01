package br.com.zup.felipe.gadelha.api.v1.controller

import br.com.zup.felipe.gadelha.DeletePixRq
import br.com.zup.felipe.gadelha.KeyManagerDeleteServiceGrpc
import br.com.zup.felipe.gadelha.KeyManagerRegisterServiceGrpc
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.PathVariable
import io.micronaut.validation.Validated
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Validated
@Controller("/api/v1/pix")
class DeletePixController(
    val grpcClient: KeyManagerDeleteServiceGrpc.KeyManagerDeleteServiceBlockingStub
) {

    @Delete("/{pixId}")
    fun delete(@PathVariable pixId: UUID, @Header("CLIENT_ID") clientId: UUID): HttpResponse<Any>{
        val result = grpcClient.delete(DeletePixRq.newBuilder()
            .setClientId(clientId.toString())
            .setPixId(pixId.toString())
            .build()
        )
        println(result)
        return HttpResponse.noContent()
    }



}