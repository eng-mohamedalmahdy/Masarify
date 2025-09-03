package com.lightfeather.data.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual val Dispatchers.IoDispatcher: CoroutineDispatcher get() = Dispatchers.IO