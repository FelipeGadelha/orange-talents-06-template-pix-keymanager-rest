package br.com.zup.felipe.gadelha.infra.integration

import br.com.zup.felipe.gadelha.KeyManagerDeleteServiceGrpc
import br.com.zup.felipe.gadelha.KeyManagerFindAllServiceGrpc
import br.com.zup.felipe.gadelha.KeyManagerFindServiceGrpc
import br.com.zup.felipe.gadelha.KeyManagerRegisterServiceGrpc
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import javax.inject.Singleton

@Factory
class GrpcClientFactory(@GrpcChannel("key-manager-grpc") val channel: ManagedChannel) {

    @Singleton
    fun registerPix(): KeyManagerRegisterServiceGrpc.KeyManagerRegisterServiceBlockingStub {
        return KeyManagerRegisterServiceGrpc.newBlockingStub(channel)
    }
    @Singleton
    fun deletePix(): KeyManagerDeleteServiceGrpc.KeyManagerDeleteServiceBlockingStub {
        return KeyManagerDeleteServiceGrpc.newBlockingStub(channel)
    }
    @Singleton
    fun findPix(): KeyManagerFindServiceGrpc.KeyManagerFindServiceBlockingStub {
        return KeyManagerFindServiceGrpc.newBlockingStub(channel)
    }
    @Singleton
    fun findAllPix(): KeyManagerFindAllServiceGrpc.KeyManagerFindAllServiceBlockingStub {
        return KeyManagerFindAllServiceGrpc.newBlockingStub(channel)
    }
}