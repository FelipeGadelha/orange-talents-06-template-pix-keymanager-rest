package br.com.zup.felipe.gadelha.api.v1.dto.response

import br.com.zup.felipe.gadelha.FindPixRs
import br.com.zup.felipe.gadelha.PixKeyType
import java.time.LocalDateTime
import java.time.ZoneOffset

data class PixDetailRs(
    val pixId: String,
    val keyPix: String,
    val holder: String,
    val cpf: String,
    val keyType: PixKeyType,
    val account: Map<String, String>,
    val createdAt: String
) {
    constructor(result: FindPixRs):
        this (
            pixId = result.pixId,
            keyPix = result.value,
            holder = result.name,
            cpf = result.cpf,
            keyType = result.keyType,
            account = mapOf<String, String>(
                "name" to result.account.name,
                "agency" to result.account.agency,
                "number" to result.account.number,
                "type" to result.account.type.toString()
            ),
            createdAt = LocalDateTime
                .ofEpochSecond(result.createdAt.seconds, result.createdAt.nanos, ZoneOffset.UTC).toString()

        )
}
