package br.com.zup.felipe.gadelha.api.controller

import br.com.zup.felipe.gadelha.infra.integration.GrpcClientFactory
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get

@Controller
class Controller(val grpcClient: GrpcClientFactory) {

    @Get
    fun test(): String {
        return "Hello-world"
    }
}