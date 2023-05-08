package domain.transaction

import domain.Account
import domain.Category

sealed class Transaction(
    val id: Int, val name: String, val description: String?, val categories: List<Category>,
    val amount: Double, val timestamp: Int, val account: Account
) {
    class Income(
        id: Int, name: String, description: String?, category: List<Category>,
        amount: Double, timestamp: Int, account: Account, val source: String
    ) : Transaction(id, name, description, category, amount, timestamp, account)

    class Expense(
        id: Int, name: String, description: String?, category: List<Category>,
        amount: Double, timestamp: Int, account: Account
    ) : Transaction(id, name, description, category, amount, timestamp, account)

    class Transfer(
        id: Int, name: String, description: String?, category: List<Category>,
        amount: Double, timestamp: Int, account: Account, val receiverAccount : Account, val fee: Double
    ) : Transaction(id, name, description, category, amount, timestamp, account)
}