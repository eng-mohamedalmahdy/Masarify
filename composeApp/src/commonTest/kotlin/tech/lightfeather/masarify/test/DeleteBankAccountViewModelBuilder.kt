package tech.lightfeather.masarify.test

import tech.lightfeather.domain.model.Account
import tech.lightfeather.domain.model.Currency
import tech.lightfeather.domain.usecase.DeleteAccount
import tech.lightfeather.masarify.navigation.Navigator
import tech.lightfeather.masarify.page.deletebankaccount.DeleteBankAccountPageViewModel

internal val testCurrency = Currency(name = "USD", sign = "$", id = 1)

internal val testAccount =
    Account(
        name = "Test Bank",
        currency = testCurrency,
        description = null,
        balance = 1000.0,
        color = "#FFFFFF",
        logo = null,
        id = 1,
    )

internal fun buildDeleteBankAccountViewModel(
    navigator: Navigator = CapturingNavigator(),
    account: Account = testAccount,
): DeleteBankAccountPageViewModel {
    val accountRepo = FakeAccountRepository()
    val currencyRepo = FakeCurrencyRepository()
    return DeleteBankAccountPageViewModel(
        navigator = navigator,
        deleteBankAccount =
            DeleteAccount(
                accountRepository = accountRepo,
                syncHelper = buildSyncEnqueueHelper(),
                currencyRepository = currencyRepo,
            ),
        toBeDeletedAccount = account,
    )
}
