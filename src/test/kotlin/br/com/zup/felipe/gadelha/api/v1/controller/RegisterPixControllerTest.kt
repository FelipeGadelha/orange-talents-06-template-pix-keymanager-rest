package br.com.zup.felipe.gadelha.api.v1.controller

import br.com.zup.felipe.gadelha.AccountType
import br.com.zup.felipe.gadelha.KeyManagerRegisterServiceGrpc
import br.com.zup.felipe.gadelha.PixKeyType
import br.com.zup.felipe.gadelha.PixRs
import br.com.zup.felipe.gadelha.api.v1.dto.request.PixRequest
import br.com.zup.felipe.gadelha.infra.integration.GrpcClientFactory
import com.github.javafaker.Faker
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
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.BDDMockito
import org.mockito.Mockito
import java.util.*
import java.util.stream.Stream

@MicronautTest
internal class RegisterPixControllerTest {
    @field:Inject
    lateinit var grpcClient: KeyManagerRegisterServiceGrpc.KeyManagerRegisterServiceBlockingStub
    @field:Inject
    @field:Client("/")
    lateinit var httpClient: HttpClient

    private val BASE_PATH = "/api/v1/pix/"

    @Factory
    @Replaces(factory = GrpcClientFactory::class)
    internal class StubFactory {
        @Singleton
        fun registerStubMock()
                = Mockito.mock(KeyManagerRegisterServiceGrpc.
        KeyManagerRegisterServiceBlockingStub::class.java)
    }

    private val pixId = UUID.randomUUID().toString()
    private val responseGrpc = PixRs.newBuilder()
        .setPixId(pixId)
        .build()

    companion object {
        private val faker = Faker(Locale("pt-BR"))

        @JvmStatic
        fun provideTestArgumentsInvalidData(): Stream<Arguments> =
            Stream.of(
                Arguments.of(PixKeyType.CPF.toString(), "123-123-765-45", AccountType.CURRENT),
                Arguments.of(PixKeyType.EMAIL.toString(), "${faker.name().firstName()}@", AccountType.SAVING),
                Arguments.of(PixKeyType.EMAIL.toString(), "${faker.name().firstName()}@email", AccountType.CURRENT),
                Arguments.of(PixKeyType.EMAIL.toString(), "${faker.name().firstName()}@@gmail", AccountType.SAVING),
                Arguments.of(PixKeyType.EMAIL.toString(), "${faker.name().firstName()}@gmail@com.com", AccountType.CURRENT),
                Arguments.of(PixKeyType.EMAIL.toString(), "${faker.name().firstName()}@outlook@com", AccountType.SAVING),
                Arguments.of(PixKeyType.PHONE.toString(), faker.phoneNumber().cellPhone(), AccountType.CURRENT),
                Arguments.of(PixKeyType.PHONE.toString(), "+55${faker.phoneNumber().cellPhone()}",AccountType.SAVING),
                Arguments.of(PixKeyType.CPF.toString(), "7189495", AccountType.SAVING),
                Arguments.of(PixKeyType.EMAIL.toString(), "felipe@", AccountType.SAVING),
            )
        @JvmStatic
        fun provideTestArgumentsSuccessfully(): Stream<Arguments> =
            Stream.of(
                Arguments.of(PixKeyType.CPF.toString(), "04537189495", AccountType.SAVING),
                Arguments.of(PixKeyType.CPF.toString(), "11343733090", AccountType.CURRENT),
                Arguments.of(PixKeyType.CPF.toString(), "25296732251", AccountType.SAVING),
                Arguments.of(PixKeyType.CPF.toString(), "31335427708", AccountType.SAVING),
                Arguments.of(PixKeyType.CPF.toString(), "44778335554", AccountType.CURRENT),
                Arguments.of(PixKeyType.CPF.toString(), "51615666710", AccountType.SAVING),
                Arguments.of(PixKeyType.CPF.toString(), "69458164516", AccountType.CURRENT),
                Arguments.of(PixKeyType.CPF.toString(), "73281228119", AccountType.SAVING),
                Arguments.of(PixKeyType.CPF.toString(), "82372374710", AccountType.CURRENT),
                Arguments.of(PixKeyType.CPF.toString(), "91611511496", AccountType.SAVING),
                Arguments.of(PixKeyType.PHONE.toString(), "+5511${Random().nextInt(999999999)}", AccountType.CURRENT),
                Arguments.of(PixKeyType.EMAIL.toString(), faker.internet().emailAddress(), AccountType.SAVING),
//                Arguments.of(PixKeyType.RANDOM.toString(), "", AccountType.CURRENT),
            )
    }

    @ParameterizedTest
    @MethodSource("provideTestArgumentsSuccessfully")
    internal fun `should register pix keys successfully`(keyType: PixKeyType, value: String, account: AccountType) {
        val pixRequest = PixRequest(
            value = value,
            keyType = keyType,
            accountType = account
        )
        BDDMockito.given(grpcClient.register(Mockito.any()))
            .willReturn(responseGrpc)

        val request = HttpRequest.POST(BASE_PATH, pixRequest)
                .header("CLIENT_ID", "c56dfef4-7901-44fb-84e2-a2cefb157890")
        val response = httpClient.toBlocking().exchange(request, Unit::class.java)
        with(response){
            assertEquals(HttpStatus.CREATED, status)
            assertTrue(headers.contains("Location"))
            assertEquals("${BASE_PATH}${pixId}", headers["Location"])
        }
    }

    @ParameterizedTest
    @MethodSource("provideTestArgumentsInvalidData")
    internal fun `should not register when invalid data`(keyType: PixKeyType, value: String, account: AccountType) {
        val pixRequest = PixRequest(
            value = value,
            keyType = keyType,
            accountType = account
        )
        BDDMockito.given(grpcClient.register(Mockito.any()))
            .willReturn(responseGrpc)

        val request = HttpRequest.POST(BASE_PATH, pixRequest)
            .header("CLIENT_ID", "c56dfef4-7901-44fb-84e2-a2cefb157890")
        val exception = assertThrows<HttpClientResponseException>{ httpClient.toBlocking().exchange(request, Unit::class.java) }
        with(exception){
            assertEquals(HttpStatus.BAD_REQUEST, status)
//            assertTrue(response.body())
        }
    }
}
