package br.com.zup.felipe.gadelha.api.validation

import br.com.zup.felipe.gadelha.PixKeyType
import br.com.zup.felipe.gadelha.api.v1.dto.request.PixRequest
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import jakarta.inject.Singleton
import java.util.*
import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [PixValueValidator::class])
annotation class PixValue(
    val message: String = "A Chave \${validatedValue.value} é inválida para o tipo \${validatedValue.keyType}",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = []
)

@Singleton
class PixValueValidator: ConstraintValidator<PixValue, PixRequest> {
    override fun isValid(
        pix: PixRequest?,
        annotationMetadata: AnnotationValue<PixValue>,
        context: ConstraintValidatorContext
    ): Boolean = when (pix?.keyType) {
        PixKeyType.CPF -> RegexValidator.CPF.validate(pix.value!!)
        PixKeyType.EMAIL -> RegexValidator.EMAIL.validate(pix.value!!)
        PixKeyType.PHONE -> RegexValidator.PHONE.validate(pix.value!!)
        PixKeyType.RANDOM -> Objects.isNull(pix.value)
        else -> true
    }
}
enum class RegexValidator(private val regex: Regex) {
    CPF("^[0-9]{11}$".toRegex()),
    EMAIL("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}\\@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+".toRegex()),
    PHONE("^\\+[1-9][0-9]\\d{1,14}\$".toRegex());
//    UUID("[a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8}".toRegex());

    fun validate(value: String):Boolean = value.matches(this.regex)
}