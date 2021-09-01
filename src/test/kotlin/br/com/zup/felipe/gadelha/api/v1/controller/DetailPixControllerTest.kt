package br.com.zup.felipe.gadelha.api.v1.controller

import br.com.zup.felipe.gadelha.AccountType
import br.com.zup.felipe.gadelha.FindPixRs
import br.com.zup.felipe.gadelha.KeyManagerFindServiceGrpc
import br.com.zup.felipe.gadelha.PixKeyType
import br.com.zup.felipe.gadelha.infra.integration.GrpcClientFactory
import com.google.protobuf.Timestamp
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
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.BDDMockito.`when`
import org.mockito.Mockito
import java.time.OffsetDateTime
import java.util.*

@MicronautTest
internal class DetailPixControllerTest{

    @field:Inject
    lateinit var grpcClient: KeyManagerFindServiceGrpc.KeyManagerFindServiceBlockingStub
    @field:Inject
    @field:Client("/")
    lateinit var httpClient: HttpClient

    private val BASE_PATH = "/api/v1/pix/"

    private val findRs = FindPixRs.newBuilder()
        .setPixId(UUID.randomUUID().toString())
        .setValue(UUID.randomUUID().toString())
        .setName("Felipe Gadelha")
        .setCpf("02467781054")
        .setKeyType(PixKeyType.CPF)
        .setCreatedAt(Timestamp.newBuilder()
            .setSeconds(OffsetDateTime.now().toEpochSecond())
            .setNanos(OffsetDateTime.now().nano)
            .build()
        )
        .setAccount(FindPixRs.AccountRs.newBuilder()
                    .setType(AccountType.CURRENT)
                    .setAgency("0001")
                    .setName("ITAÚ UNIBANCO S.A.")
                    .setNumber("291900")
                .build()
        )
        .build()

    @Factory
    @Replaces(factory = GrpcClientFactory::class)
    internal class StubFactory {
        @Singleton
        fun registerStubMock()
                = Mockito.mock(
            KeyManagerFindServiceGrpc.
            KeyManagerFindServiceBlockingStub::class.java)
    }

    @Test
    internal fun `should return details of a pix key successfully`() {
        `when`(grpcClient.find(Mockito.any())).thenReturn(findRs)

        val request = HttpRequest.GET<Any>(BASE_PATH + findRs.pixId)
            .header("CLIENT_ID", "c56dfef4-7901-44fb-84e2-a2cefb157890")
        val response = httpClient.toBlocking().exchange(request, Any::class.java)

        with(response) {
            assertEquals(HttpStatus.OK, status)
        }
    }

    @Test
    internal fun `should not return details of pix key when pixId not found`() {
        `when`(grpcClient.find(Mockito.any())).thenThrow(StatusRuntimeException(Status.NOT_FOUND))

        val request = HttpRequest.GET<Any>(BASE_PATH + findRs.pixId)
            .header("CLIENT_ID", "c56dfef4-7901-44fb-84e2-a2cefb157890")
        val exception = assertThrows<HttpClientResponseException>{ httpClient.toBlocking().exchange(request, Any::class.java) }

        with(exception) {
            assertEquals(HttpStatus.NOT_FOUND, status)
        }
    }
}

