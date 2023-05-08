package usecase

import data.repository.AccountRepository
import data.repository.TransferRepository
import domain.Account

class CreateAccount(private val accountRepository: AccountRepository) {
    operator fun invoke(account: Account) = accountRepository.createAccount(account)
}

class UpdateAccountName(private val accountRepository: AccountRepository) {
    operator fun invoke(account: Account) = accountRepository.updateAccountName(account)
}

class DeleteAccount(private val accountRepository: AccountRepository) {
    operator fun invoke(account: Account) = accountRepository.deleteAccount(account)
}

class Transfer(private val transferRepository: TransferRepository) {
    operator fun invoke(senderAccount: Account, receiverAccount: Account, amount: Double, fee: Double) =
        transferRepository.transfer(senderAccount, receiverAccount, amount, fee)
}


