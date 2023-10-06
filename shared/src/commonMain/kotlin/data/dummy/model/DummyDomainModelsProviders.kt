package data.dummy.model

import com.lightfeather.core.domain.Account
import com.lightfeather.core.domain.Category
import com.lightfeather.core.domain.Currency
import com.lightfeather.core.domain.transaction.Transaction

object DummyDomainModelsProviders {
    val bankAccount = Account(
        id = 3311,
        name = "Romeo Higgins",
        currency = Currency(id = 5278, name = "Phil Browning", sign = "elementum"),
        description = "Desc",
        balance = 18.19,
        color = "integer",
        logo = "quaeque"
    )

    val category = Category(id = 8823, name = "Mack Everett", description = "null", color = "#000000", icon = "https://img.icons8.com/small/512/jake.png")

    val currency = Currency(id = 3008, name = "Nina Nieves", sign = "signiferumque")

    val expense = Transaction.Expense(
        id = 7518,
        name = "Angelique Hartman",
        description = "asdadas",
        category = listOf(category),
        amount = 24.25,
        timestamp = 4107,
        account = bankAccount
    )

    val income = Transaction.Income(
        id = 4670,
        name = "Johnnie McKinney",
        description = "asdasdadsad",
        category = listOf(category),
        amount = 32.33,
        timestamp = 1838,
        account = Account(
            id = 7172,
            name = "Javier Small",
            currency = currency,
            description = "asdadadasd",
            balance = 34.35,
            color = "senserit",
            logo = "lorem"
        ),
        source = "definitiones"
    )

    val transfer = Transaction.Transfer(
        id = 4232,
        name = "Grace Valencia",
        description = "asdsada",
        amount = 44.45,
        timestamp = 2447,
        account = Account(
            id = 9272,
            name = "Clement Tyler",
            currency = Currency(id = 8013, name = "Carmella Poole", sign = "tempor"),
            description = "asdadadasd",
            balance = 46.47,
            color = "pericula",
            logo = "eloquentiam"
        ),
        receiverAccount = Account(
            id = 2439, name = "Lucio Larson", currency = Currency(
                id = 8370,
                name = "Bernadine Waters",
                sign = "definitiones"
            ), description = null, balance = 48.49, color = "invenire", logo = "ipsum"
        ),
        fee = 50.51
    )
}