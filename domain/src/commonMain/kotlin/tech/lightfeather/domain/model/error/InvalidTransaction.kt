package tech.lightfeather.domain.model.error

data class InvalidTransaction(
    override val message: String,
) : AppError
