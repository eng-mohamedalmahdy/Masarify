package com.lightfeather.data.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

actual val Dispatchers.IoDispatcher: CoroutineDispatcher
    get() = Dispatchers.IO
