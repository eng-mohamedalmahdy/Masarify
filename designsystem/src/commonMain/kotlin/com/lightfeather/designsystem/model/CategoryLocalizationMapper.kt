package com.lightfeather.designsystem.model

import androidx.compose.runtime.Composable
import com.lightfeather.designsystem.MR
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource

/**
 * Get the localized name for a category
 * If the category is a default category with a resource key, returns the localized string
 * Otherwise, returns the category name as-is
 */
@Composable
fun UiCategory.getLocalizedName(): String =
    if (isDefault && resourceKey != null) {
        val stringRes = getStringResourceByKey(resourceKey)
        stringResource(stringRes)
    } else {
        name
    }

/**
 * Get the localized description for a category
 * If the category is a default category with a resource key, returns the localized description
 * Otherwise, returns the category description as-is
 */
@Composable
fun UiCategory.getLocalizedDescription(): String? =
    if (isDefault && resourceKey != null && description != null) {
        val descKey = "${resourceKey}_desc"
        val stringRes = getStringResourceByKey(descKey)
        stringResource(stringRes)
    } else {
        description
    }

/**
 * Map resource key to MR.strings StringResource
 * This uses reflection-like approach via when statement to map keys to actual string resources
 */
@Suppress("CyclomaticComplexMethod") // Large when statement for resource mapping is acceptable
fun getStringResourceByKey(key: String): StringResource =
    when (key) {
        // Transfer
        "category_transfer" -> MR.strings.category_transfer
        "category_transfer_desc" -> MR.strings.category_transfer_desc

        // Income Categories
        "category_salary" -> MR.strings.category_salary
        "category_salary_desc" -> MR.strings.category_salary_desc
        "category_freelance" -> MR.strings.category_freelance
        "category_freelance_desc" -> MR.strings.category_freelance_desc
        "category_investments" -> MR.strings.category_investments
        "category_investments_desc" -> MR.strings.category_investments_desc
        "category_gift" -> MR.strings.category_gift
        "category_gift_desc" -> MR.strings.category_gift_desc

        // Expense Categories
        "category_groceries" -> MR.strings.category_groceries
        "category_groceries_desc" -> MR.strings.category_groceries_desc
        "category_transportation" -> MR.strings.category_transportation
        "category_transportation_desc" -> MR.strings.category_transportation_desc
        "category_utilities" -> MR.strings.category_utilities
        "category_utilities_desc" -> MR.strings.category_utilities_desc
        "category_dining" -> MR.strings.category_dining
        "category_dining_desc" -> MR.strings.category_dining_desc
        "category_entertainment" -> MR.strings.category_entertainment
        "category_entertainment_desc" -> MR.strings.category_entertainment_desc
        "category_healthcare" -> MR.strings.category_healthcare
        "category_healthcare_desc" -> MR.strings.category_healthcare_desc
        "category_education" -> MR.strings.category_education
        "category_education_desc" -> MR.strings.category_education_desc
        "category_shopping" -> MR.strings.category_shopping
        "category_shopping_desc" -> MR.strings.category_shopping_desc
        "category_subscriptions" -> MR.strings.category_subscriptions
        "category_subscriptions_desc" -> MR.strings.category_subscriptions_desc
        "category_rent" -> MR.strings.category_rent
        "category_rent_desc" -> MR.strings.category_rent_desc
        "category_insurance" -> MR.strings.category_insurance
        "category_insurance_desc" -> MR.strings.category_insurance_desc
        "category_other" -> MR.strings.category_other
        "category_other_desc" -> MR.strings.category_other_desc

        // Fallback - return a placeholder string resource
        else -> MR.strings.category_other
    }
