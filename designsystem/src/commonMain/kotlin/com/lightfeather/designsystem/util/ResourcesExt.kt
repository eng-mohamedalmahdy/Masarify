package com.lightfeather.designsystem.util

import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.PluralsResource
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.Plural
import dev.icerock.moko.resources.desc.PluralFormatted
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc

@Composable
fun stringResource(resource: StringResource?): String? = resource?.let { StringDesc.Resource(it) }?.localized()

@Composable
fun stringResource(
    resource: StringResource?,
    vararg args: Any,
): String? = resource?.let { StringDesc.ResourceFormatted(it, *args) }?.localized()

@Composable
fun stringResource(
    resource: PluralsResource?,
    quantity: Int,
): String? = resource?.let { StringDesc.Plural(it, quantity) }?.localized()

@Composable
fun stringResource(
    resource: PluralsResource?,
    quantity: Int,
    vararg args: Any,
): String? = resource?.let { StringDesc.PluralFormatted(it, quantity, *args) }?.localized()
