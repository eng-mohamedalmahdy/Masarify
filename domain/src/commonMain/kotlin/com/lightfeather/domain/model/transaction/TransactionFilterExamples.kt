package com.lightfeather.domain.model.transaction

import com.lightfeather.domain.model.Account
import com.lightfeather.domain.model.Category
import com.lightfeather.domain.model.Currency
import kotlinx.datetime.LocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Examples demonstrating the enhanced TransactionFilter system with DSL builder
 */
object TransactionFilterExamples {
    // Sample data for demonstrations
    private val usdCurrency = Currency("USD", "$", 1)
    private val eurCurrency = Currency("EUR", "€", 2)

    private val checkingAccount = Account("Checking", usdCurrency, "Primary checking account", 1000.0, "#4CAF50", "", 1)
    private val savingsAccount = Account("Savings", usdCurrency, "Savings account", 5000.0, "#2196F3", "", 2)

    private val foodCategory = Category(1, "Food & Dining", "Restaurant and grocery expenses", "#FF9800", "food")
    private val transportCategory = Category(2, "Transportation", "Gas, public transport", "#9C27B0", "transport")

    /**
     * Example 1: Filter by account and amount range
     */
    fun filterByAccountAndAmountRange(): TransactionFilter =
        transactionFilter {
            accountIn(checkingAccount)
            amountBetween(50.0, 200.0)
        }

    /**
     * Example 2: Filter by multiple categories and transaction types
     */
    fun filterByCategoriesAndTypes(): TransactionFilter =
        transactionFilter {
            categoryIn(foodCategory, transportCategory)
            transactionTypeIn(
                TransactionFilter.TransactionType.EXPENSE,
                TransactionFilter.TransactionType.INCOME,
            )
        }

    /**
     * Example 3: Filter by date range (last 30 days)
     */
    fun filterByDateRange(): TransactionFilter {
        val today = LocalDateTime(2024, 1, 15, 12, 0, 0)
        val monthAgo = LocalDateTime(2023, 12, 15, 12, 0, 0)

        return transactionFilter {
            dateRange(monthAgo, today)
        }
    }

    /**
     * Example 4: Text search with attachment filter
     */
    fun filterByTextAndAttachments(): TransactionFilter =
        transactionFilter {
            textContains("coffee")
            hasAttachments()
        }

    /**
     * Example 5: Complex filter with OR logic
     */
    fun complexFilterWithOrLogic(): TransactionFilter =
        transactionFilter {
            useOrLogic()
            amountGreaterThan(1000.0)
            categoryIn(foodCategory)
            textContains("emergency")
        }

    /**
     * Example 6: Filter only expenses above $100 in specific accounts
     */
    fun expenseFilterAdvanced(): TransactionFilter =
        transactionFilter {
            expenseOnly()
            accountIn(checkingAccount, savingsAccount)
            amountGreaterThan(100.0)
        }

    /**
     * Example 7: Filter by currency and amount range
     */
    fun filterByCurrencyAndRange(): TransactionFilter =
        transactionFilter {
            currencyIn(usdCurrency)
            amountBetween(10.0, 500.0)
            noAttachments()
        }

    /**
     * Example 8: Income only filter with date constraints
     */
    fun incomeFilterWithDates(): TransactionFilter {
        val startOfYear = LocalDateTime(2024, 1, 1, 0, 0, 0)

        return transactionFilter {
            incomeOnly()
            dateAfter(startOfYear)
            amountGreaterThan(500.0)
        }
    }

    /**
     * Example 9: Transfer filter with specific accounts
     */
    fun transferFilterSpecific(): TransactionFilter =
        transactionFilter {
            transferOnly()
            accountIn(checkingAccount) // From checking account
            amountLessThan(1000.0)
        }

    /**
     * Example 10: Empty filter (matches all transactions)
     */
    fun emptyFilter(): TransactionFilter = TransactionFilter.EMPTY

    /**
     * Demonstration of filter usage with sample transactions
     */
    @OptIn(ExperimentalTime::class)
    fun demonstrateFilterUsage() {
        // Create some sample transactions
        val expense =
            Transaction.Expense(
                id = 1,
                name = "Coffee Shop Purchase",
                description = "Morning coffee",
                amount = 5.50,
                timestamp = Clock.System.now().toEpochMilliseconds(),
                account = checkingAccount,
                categories = listOf(foodCategory),
                attachments = emptyList(),
            )

        val income =
            Transaction.Income(
                id = 2,
                name = "Salary",
                description = "Monthly salary payment",
                amount = 5000.0,
                timestamp = Clock.System.now().toEpochMilliseconds(),
                account = checkingAccount,
                source = Category(10, "Salary", "Employment income", "#4CAF50", "salary"),
                attachments = emptyList(),
            )

        // Test different filters
        val accountFilter = filterByAccountAndAmountRange()
        val categoryFilter = filterByCategoriesAndTypes()
        val textFilter = filterByTextAndAttachments()

        // Check if transactions match filters
        println("Expense matches account filter: ${accountFilter.matches(expense)}")
        println("Income matches category filter: ${categoryFilter.matches(income)}")
        println("Expense matches text filter: ${textFilter.matches(expense)}")

        // Show filter descriptions
        println("Account filter is empty: ${accountFilter.isEmpty()}")
        println("Empty filter matches expense: ${emptyFilter().matches(expense)}")
    }
}

/**
 * Usage examples for different filtering scenarios
 */
object FilterUsageExamples {
    /**
     * Common filtering patterns for financial apps
     */

    // Show all expenses from last month over $50
    val recentExpensesFilter =
        transactionFilter {
            expenseOnly()
            amountGreaterThan(50.0)
            // dateAfter would be calculated for last month
        }

    // Show all transfers between specific accounts
    val transfersFilter =
        transactionFilter {
            transferOnly()
            // accountIn would include specific accounts
        }

    // Show all transactions with receipts (attachments)
    val transactionsWithReceiptsFilter =
        transactionFilter {
            hasAttachments()
        }

    // Show high-value transactions (over $1000)
    val highValueFilter =
        transactionFilter {
            amountGreaterThan(1000.0)
            useOrLogic()
            transactionTypeIn(
                TransactionFilter.TransactionType.EXPENSE,
                TransactionFilter.TransactionType.TRANSFER,
            )
        }

    // Budget tracking: Show all food expenses
    val foodBudgetFilter =
        transactionFilter {
            expenseOnly()
            // categoryIn would include food categories
        }
}
