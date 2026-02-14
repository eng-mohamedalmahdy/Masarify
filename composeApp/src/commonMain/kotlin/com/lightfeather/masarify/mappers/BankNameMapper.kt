package com.lightfeather.masarify.mappers

import com.lightfeather.designsystem.model.UiBankName
import com.lightfeather.domain.model.BankName

fun BankName.toUiBankName(): UiBankName =
    UiBankName(
        id = id.toString(),
        name = name,
        resourceKey = resourceKey,
        logoUrl = logoUrl,
        isDefault = isDefault,
    )

fun UiBankName.toBankName(): BankName =
    BankName(
        id = id.toIntOrNull() ?: -1,
        name = name,
        resourceKey = resourceKey,
        logoUrl = logoUrl,
        isDefault = isDefault,
    )
