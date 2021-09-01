package br.com.zup.felipe.gadelha.api.v1.controller

import br.com.zup.felipe.gadelha.*
import br.com.zup.felipe.gadelha.api.v1.dto.response.PixRs
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.validation.Validated
import java.util.*

@Validated
@Controller("/api/v1/pix")
class FindAllPixController(
    val grpcClient: KeyManagerFindAllServiceGrpc.KeyManagerFindAllServiceBlockingStub
) {
    @Get
    fun findAll(@Header("CLIENT_ID") clientId: UUID): HttpResponse<Any>{
        val result = grpcClient.findAll(FindAllPixRq.newBuilder()
            .setClientId(clientId.toString())
            .build()
        )
        return HttpResponse.ok(result.pixKeysList.map { PixRs(it) })
    }
}