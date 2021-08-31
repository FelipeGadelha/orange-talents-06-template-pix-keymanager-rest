package br.com.zup.felipe.gadelha.infra.integration

import br.com.zup.felipe.gadelha.KeyManagerDeleteServiceGrpc
import br.com.zup.felipe.gadelha.KeyManagerFindAllServiceGrpc
import br.com.zup.felipe.gadelha.KeyManagerFindServiceGrpc
import br.com.zup.felipe.gadelha.KeyManagerRegisterServiceGrpc
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import jakarta.inject.Singleton

@Factory
class GrpcClientFactory(@GrpcChannel("key-manager-grpc") val channel: ManagedChannel) {

    @Singleton
    fun registerPix() = KeyManagerRegisterServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun deletePix() = KeyManagerDeleteServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun findPix() = KeyManagerFindServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun findAllPix() = KeyManagerFindAllServiceGrpc.newBlockingStub(channel)
}