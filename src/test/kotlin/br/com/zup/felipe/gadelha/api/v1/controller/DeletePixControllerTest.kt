package br.com.zup.felipe.gadelha.api.v1.controller

import br.com.zup.felipe.gadelha.DeletePixRs
import br.com.zup.felipe.gadelha.KeyManagerDeleteServiceGrpc
import br.com.zup.felipe.gadelha.infra.integration.GrpcClientFactory
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.BDDMockito
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.util.*
import java.util.stream.Stream


@MicronautTest
internal class DeletePixControllerTest {
    @field:Inject
    lateinit var grpcClient: KeyManagerDeleteServiceGrpc.KeyManagerDeleteServiceBlockingStub
    @field:Inject
    @field:Client("/")
    lateinit var httpClient: HttpClient

    private val BASE_PATH = "/api/v1/pix/"

    @Factory
    @Replaces(factory = GrpcClientFactory::class)
    internal class StubFactory {
        @Singleton
        fun registerStubMock()
                = Mockito.mock(
            KeyManagerDeleteServiceGrpc.
            KeyManagerDeleteServiceBlockingStub::class.java)
    }

    @Test
    internal fun `should delete pix key successfully`() {
        BDDMockito.reset(grpcClient)
        given(grpcClient.delete(Mockito.any())).willReturn(DeletePixRs.newBuilder().build())

        val request = HttpRequest.DELETE(BASE_PATH + UUID.randomUUID().toString(), Any::class.java)
            .header("CLIENT_ID", "c56dfef4-7901-44fb-84e2-a2cefb157890")

        val response = httpClient.toBlocking().exchange(request, Any::class.java)
        with(response) {
            assertEquals(HttpStatus.NO_CONTENT, status)
        }
    }
    @Test
    internal fun `should not delete pix key when invalid data`() {
        `when`(grpcClient.delete(Mockito.any())).thenThrow(StatusRuntimeException(Status.ALREADY_EXISTS))
        val request = HttpRequest.DELETE(BASE_PATH + UUID.randomUUID().toString(), Any::class.java)
            .header("CLIENT_ID", UUID.randomUUID().toString())
        val exception = assertThrows<HttpClientResponseException>{ httpClient.toBlocking().exchange(request, Any::class.java) }
        with(exception) {
            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, status)
        }
    }
}