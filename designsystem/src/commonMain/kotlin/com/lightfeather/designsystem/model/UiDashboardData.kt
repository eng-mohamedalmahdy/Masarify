package com.lightfeather.designsystem.model

/**
 * Quick stats for different account types (cash, debit, credit)
 *
 * @property cash Total balance across cash accounts
 * @property debit Total balance across debit accounts
 * @property credit Total balance across credit accounts
 */
data class UiQuickStats(
    val cash: String = "",
    val debit: String = "",
    val credit: String = "",
)

/**
 * Spending analytics data including spending vs saving breakdown
 *
 * @property spendingPercentage Percentage of spending (0.0 to 1.0)
 * @property savingPercentage Percentage of saving (0.0 to 1.0)
 * @property totalSpending Total amount spent formatted as string
 * @property totalSaving Total amount saved formatted as string
 * @property categoryBreakdown List of spending by category
 */
data class UiSpendingAnalytics(
    val spendingPercentage: Float = 0f,
    val savingPercentage: Float = 0f,
    val totalSpending: String = "",
    val totalSaving: String = "",
    val categoryBreakdown: List<UiCategorySpending> = emptyList(),
) {
    companion object
}

/**
 * Spending amount for a specific category
 *
 * @property category The category details
 * @property amount Formatted amount string
 * @property percentage Percentage of total spending (0.0 to 1.0)
 */
data class UiCategorySpending(
    val category: UiCategory,
    val amount: String,
    val percentage: Float,
) {
    companion object {
        val dummy =
            UiCategorySpending(
                category = UiCategory.dummy,
                amount = "500.00",
                percentage = 0.4f,
            )
    }
}

/**
 * Companion object for UiSpendingAnalytics with dummy data for previews
 */
fun UiSpendingAnalytics.Companion.dummyInstance() =
    UiSpendingAnalytics(
        spendingPercentage = 0.6f,
        savingPercentage = 0.4f,
        totalSpending = "2,500.00",
        totalSaving = "1,500.00",
        categoryBreakdown =
            listOf(
                UiCategorySpending.dummy,
                UiCategorySpending.dummy.copy(percentage = 0.3f, amount = "375.00"),
                UiCategorySpending.dummy.copy(percentage = 0.3f, amount = "375.00"),
            ),
    )

private val UiSpendingAnalytics.Companion.dummy: UiSpendingAnalytics
    get() = dummyInstance()
