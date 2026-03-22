package com.lightfeather.masarify.widget

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import platform.Foundation.NSUserDefaults

actual class WidgetDataSyncService {
    actual fun syncExpenseCategories(categories: List<WidgetCategory>) {
        val json = Json.encodeToString(categories)
        val defaults = NSUserDefaults(suiteName = APP_GROUP)
        defaults?.setObject(json, forKey = KEY_EXPENSE_CATEGORIES)
        defaults?.synchronize()
    }

    companion object {
        const val APP_GROUP = "group.com.lightfeather.masarify"
        const val KEY_EXPENSE_CATEGORIES = "expense_categories"
    }
}
