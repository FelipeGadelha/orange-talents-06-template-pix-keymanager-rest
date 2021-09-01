package br.com.zup.felipe.gadelha.api.v1.dto.request

import br.com.zup.felipe.gadelha.AccountType
import br.com.zup.felipe.gadelha.PixKeyType
import br.com.zup.felipe.gadelha.PixRq
import br.com.zup.felipe.gadelha.api.validation.PixValue
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Introspected
@PixValue
data class PixRequest(
    @field:Size(max = 77)
    val value: String?,
    @field:NotNull
    val keyType: PixKeyType,
    @field:NotNull
    val accountType: AccountType,
) {
    fun convertGrpc(clientId: UUID): PixRq =
        PixRq.newBuilder()
            .setClientId(clientId.toString())
            .setKeyType(keyType)
            .setValue(value ?: "")
            .setAccountType(accountType)
            .build()
}
