package com.lightfeather.core.usecase

import com.lightfeather.core.data.repository.AccountRepository
import com.lightfeather.core.data.repository.TransferRepository
import com.lightfeather.core.domain.Account

class CreateAccount(private val accountRepository: AccountRepository) {
    suspend operator fun invoke(account: Account) = accountRepository.createAccount(account)
}

class UpdateAccountName(private val accountRepository: AccountRepository) {
    suspend operator fun invoke(account: Account) = accountRepository.updateAccountName(account)
}

class DeleteAccount(private val accountRepository: AccountRepository) {
    suspend operator fun invoke(account: Account) = accountRepository.deleteAccount(account)
}

class Transfer(private val transferRepository: TransferRepository) {
    suspend operator fun invoke(senderAccount: Account, receiverAccount: Account, amount: Double, fee: Double) =
        transferRepository.transfer(senderAccount, receiverAccount, amount, fee)
}

class GetAllAccounts(private val repository: AccountRepository) {
    operator fun invoke() = repository.getAccounts()
}

class GetBankAccountById(private val repository: AccountRepository) {
    operator fun invoke(accountId: Int) = repository.getAccountById(accountId)
}