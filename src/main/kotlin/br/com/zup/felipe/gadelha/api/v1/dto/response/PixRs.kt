package br.com.zup.felipe.gadelha.api.v1.dto.response

import br.com.zup.felipe.gadelha.AccountType
import br.com.zup.felipe.gadelha.FindAllPixRs
import br.com.zup.felipe.gadelha.PixKeyType
import java.time.LocalDateTime
import java.time.ZoneOffset

data class PixRs(
    val pixId: String,
    val keyPix: String,
    val keyType: PixKeyType,
    val accountType: AccountType,
    val createdAt: String
) {
    constructor(result: FindAllPixRs.KeyRs):
        this (
            pixId = result.pixId,
            keyPix = result.value,
            keyType = result.keyType,
            accountType = result.accountType,
            createdAt = LocalDateTime
                .ofEpochSecond(result.createdAt.seconds, result.createdAt.nanos, ZoneOffset.UTC).toString()
        )
}
