package com.lightfeather.masarify.template.transactionspane

import androidx.lifecycle.ViewModel
import com.lightfeather.domain.model.transaction.TransactionFilter

expect class TransactionsPaneViewModel : ViewModel {
    /**
     * Update the filter and reload transactions
     * This will recreate the paging source with the new filter
     */
    fun updateFilter(filter: TransactionFilter)
}
