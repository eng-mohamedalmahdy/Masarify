package com.lightfeather.masarify.template.transactionspane

import androidx.lifecycle.ViewModel
import com.lightfeather.designsystem.model.UiTransactionFilter
import kotlinx.coroutines.flow.StateFlow

expect class TransactionsPanePageViewModel : ViewModel {
    val isEmpty: StateFlow<Boolean>
    val isFiltered: StateFlow<Boolean>

    fun updateFilter(filter: UiTransactionFilter)
}
