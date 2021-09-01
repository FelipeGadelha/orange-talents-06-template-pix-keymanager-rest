package br.com.zup.felipe.gadelha.api.v1.controller

import br.com.zup.felipe.gadelha.*
import br.com.zup.felipe.gadelha.infra.integration.GrpcClientFactory
import com.github.javafaker.Faker
import com.google.protobuf.Timestamp
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.mockito.BDDMockito.*
import org.mockito.Mockito
import java.time.OffsetDateTime
import java.util.*

@MicronautTest
internal class FindAllPixControllerTest{
    @field:Inject
    lateinit var grpcClient: KeyManagerFindAllServiceGrpc.KeyManagerFindAllServiceBlockingStub
    @field:Inject
    @field:Client("/")
    lateinit var httpClient: HttpClient

    private val BASE_PATH = "/api/v1/pix/"

    private val faker: Faker = Faker(Locale("pt-BR"))

    private val findRs = FindPixRs.newBuilder()
        .setPixId(UUID.randomUUID().toString())
        .setValue(UUID.randomUUID().toString())
        .setName("Felipe Gadelha")
        .setCpf("02467781054")
        .setKeyType(PixKeyType.CPF)
        .setCreatedAt(
            Timestamp.newBuilder()
            .setSeconds(OffsetDateTime.now().toEpochSecond())
            .setNanos(OffsetDateTime.now().nano)
            .build()
        )
        .setAccount(
            FindPixRs.AccountRs.newBuilder()
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
            KeyManagerFindAllServiceGrpc.
            KeyManagerFindAllServiceBlockingStub::class.java)
    }

    private fun pixList(clientId: String) =
        FindAllPixRs.newBuilder().addAllPixKeys(
        listOf(
            buildKeyRs(value = "+5501154343543", keyType = PixKeyType.PHONE, clientId),
            buildKeyRs(value = faker.internet().emailAddress(), keyType = PixKeyType.EMAIL, clientId),
            buildKeyRs(value = "31154343543", keyType = PixKeyType.CPF, clientId),
            buildKeyRs(value = faker.internet().emailAddress(), keyType = PixKeyType.EMAIL, clientId),
            buildKeyRs(value = UUID.randomUUID().toString(), keyType = PixKeyType.RANDOM, clientId),
            buildKeyRs(value = faker.internet().emailAddress(), keyType = PixKeyType.EMAIL, clientId),
        )
    ).build()

    @Test
    internal fun `should return pix key list successfully`() {
        val clientId = UUID.randomUUID().toString()
        val list = pixList(clientId)
        `when`(grpcClient.findAll(any())).thenReturn(list)

        val request = HttpRequest.GET<Any>(BASE_PATH)
            .header("CLIENT_ID", clientId)

        val response = httpClient.toBlocking().exchange(request, List::class.java)

        with(response) {
            assertEquals(HttpStatus.OK, status)
            assertTrue(body().isNotEmpty())
            assertEquals(list.pixKeysCount, body().size)
        }
    }

    @Test
    internal fun `should find empty pix key list when clientId has no keys`() {
        val clientId = UUID.randomUUID().toString()
        `when`(grpcClient.findAll(any()))
            .thenReturn(FindAllPixRs.newBuilder().build())

        val request = HttpRequest.GET<Any>(BASE_PATH)
            .header("CLIENT_ID", clientId)

        val response = httpClient.toBlocking().exchange(request, List::class.java)

        with(response) {
            assertEquals(HttpStatus.OK, status)
            assertTrue(body().isEmpty())
        }
    }

    private fun buildKeyRs(value: String, keyType: PixKeyType, clientId: String): FindAllPixRs.KeyRs? {
        return FindAllPixRs.KeyRs.newBuilder()
                .setPixId(UUID.randomUUID().toString())
                .setClientId(clientId)
                .setValue(value)
                .setKeyType(keyType)
                .setAccountType(AccountType.SAVING)
                .setCreatedAt(Timestamp.newBuilder()
                    .setSeconds(OffsetDateTime.now().toEpochSecond())
                    .setNanos(OffsetDateTime.now().nano)
                    .build()
                )
            .build()
    }

}