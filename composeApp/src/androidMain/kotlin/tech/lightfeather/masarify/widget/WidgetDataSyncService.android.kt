package tech.lightfeather.masarify.widget

import android.content.Context
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

actual class WidgetDataSyncService(
    private val context: Context,
) {
    actual fun syncExpenseCategories(categories: List<WidgetCategory>) {
        val json = Json.encodeToString(categories)
        context
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_EXPENSE_CATEGORIES, json)
            .apply()
    }

    companion object {
        const val PREFS_NAME = "masarify_widget_prefs"
        const val KEY_EXPENSE_CATEGORIES = "expense_categories"
    }
}
