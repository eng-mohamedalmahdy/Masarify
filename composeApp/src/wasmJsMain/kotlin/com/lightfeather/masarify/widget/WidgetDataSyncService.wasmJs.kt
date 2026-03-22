package com.lightfeather.masarify.widget

actual class WidgetDataSyncService {
    actual fun syncExpenseCategories(categories: List<WidgetCategory>) {
        // No-op on Web/WASM
    }
}
