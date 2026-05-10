package tech.lightfeather.data.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

expect val Dispatchers.IoDispatcher: CoroutineDispatcher
