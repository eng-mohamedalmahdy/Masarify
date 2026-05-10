package tech.lightfeather.masarify.navigation

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import tech.lightfeather.masarify.navigation.routes.AccountsRoute
import tech.lightfeather.masarify.navigation.routes.CategoriesRoute
import tech.lightfeather.masarify.navigation.routes.DashboardRoute
import tech.lightfeather.masarify.navigation.routes.DeleteAccountRoute
import tech.lightfeather.masarify.navigation.routes.DeleteCategoryRoute
import tech.lightfeather.masarify.navigation.routes.MoreRoute
import tech.lightfeather.masarify.navigation.routes.OnBoardingRoute
import tech.lightfeather.masarify.navigation.routes.SplashRoute
import tech.lightfeather.masarify.navigation.routes.TransactionsRoute
import tech.lightfeather.masarify.page.bankaccounts.AddBankAccount
import tech.lightfeather.masarify.page.bankaccounts.BankAccountsList
import tech.lightfeather.masarify.page.bankaccounts.UpdateBankAccount
import tech.lightfeather.masarify.page.bankaccounts.ViewBankAccount
import tech.lightfeather.masarify.page.categories.AddEditCategory
import tech.lightfeather.masarify.page.categories.CategoriesList
import tech.lightfeather.masarify.page.dashboard.DashboardList
import tech.lightfeather.masarify.page.transactions.AddTransaction
import tech.lightfeather.masarify.page.transactions.EditTransaction
import tech.lightfeather.masarify.page.transactions.TransactionsList
import tech.lightfeather.masarify.page.transactions.ViewTransaction
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

/**
 * Centralized navigation registry for all NavKey types in the application.
 * This is the single source of truth for navigation serialization configuration.
 *
 * All routes (main app routes and nested list-detail routes) are registered here
 * to enable state preservation across process death and configuration changes.
 */
object NavigationRegistry {
    /**
     * Serializers module containing all NavKey subclass registrations.
     * Used for polymorphic serialization of navigation state.
     */
    val serializersModule =
        SerializersModule {
            polymorphic(NavKey::class) {
                // Main app routes
                subclass(SplashRoute::class, SplashRoute.serializer())
                subclass(OnBoardingRoute::class, OnBoardingRoute.serializer())
                subclass(DashboardRoute::class, DashboardRoute.serializer())
                subclass(AccountsRoute::class, AccountsRoute.serializer())
                subclass(TransactionsRoute::class, TransactionsRoute.serializer())
                subclass(MoreRoute::class, MoreRoute.serializer())
                subclass(CategoriesRoute::class, CategoriesRoute.serializer())
                subclass(DeleteAccountRoute::class, DeleteAccountRoute.serializer())
                subclass(DeleteCategoryRoute::class, DeleteCategoryRoute.serializer())

                // BankAccounts list-detail routes
                subclass(BankAccountsList::class, BankAccountsList.serializer())
                subclass(AddBankAccount::class, AddBankAccount.serializer())
                subclass(ViewBankAccount::class, ViewBankAccount.serializer())
                subclass(UpdateBankAccount::class, UpdateBankAccount.serializer())

                // Categories list-detail routes
                subclass(CategoriesList::class, CategoriesList.serializer())
                subclass(AddEditCategory::class, AddEditCategory.serializer())
                subclass(TransactionsList::class, TransactionsList.serializer())
                subclass(ViewTransaction::class, ViewTransaction.serializer())
                subclass(AddTransaction::class, AddTransaction.serializer())
                subclass(EditTransaction::class, EditTransaction.serializer())
                subclass(DashboardList::class, DashboardList.serializer())
            }
        }

    /**
     * Saved state configuration for navigation back stacks.
     * Reused across all navigation contexts (main app and nested list-detail).
     */
    val savedStateConfiguration =
        SavedStateConfiguration {
            serializersModule = this@NavigationRegistry.serializersModule
        }
}
