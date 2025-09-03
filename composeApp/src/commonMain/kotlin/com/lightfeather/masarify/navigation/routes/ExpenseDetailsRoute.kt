package com.lightfeather.happytail.navigation.routes

import com.lightfeather.happytail.domain.model.DomainExpense
import com.lightfeather.happytail.navigation.Route
import kotlinx.serialization.Serializable

@Serializable
class ExpenseDetailsRoute(val expense : DomainExpense,) : Route() {

    override val route: String = "expensedetails"
}