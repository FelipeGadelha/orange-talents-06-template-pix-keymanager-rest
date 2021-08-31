package br.com.zup.felipe.gadelha.api.v1.controller

import br.com.zup.felipe.gadelha.*
import br.com.zup.felipe.gadelha.api.v1.dto.response.PixDetailRs
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.validation.Validated
import java.util.*

@Validated
@Controller("/api/v1/pix")
class DetailPixController(
    val grpcClient: KeyManagerFindServiceGrpc.KeyManagerFindServiceBlockingStub
) {

    @Get("/{pixId}")
    fun delete(@PathVariable pixId: UUID, @Header("CLIENT_ID") clientId: UUID): HttpResponse<Any>{
        val result = grpcClient.find(FindPixRq.newBuilder().setPixId(
            FindPixRq.FindByPixId.newBuilder()
                .setClientId(clientId.toString())
                .setPixId(pixId.toString())
                .build()
            ).build()
        )
        println(result)
        return HttpResponse.ok(PixDetailRs(result))
    }
}