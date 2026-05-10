package tech.lightfeather.domain.model

/**
 * Predefined default categories that are seeded on first app launch
 * Each category has a resource key for localization support
 */
enum class DefaultCategory(
    val resourceKey: String,
    val descriptionKey: String,
    val color: String,
    val icon: String,
) {
    // Income Categories
    SALARY(
        resourceKey = "category_salary",
        descriptionKey = "category_salary_desc",
        color = "#4CAF50",
        icon = "https://img.icons8.com/color/512/money-bag.png",
    ),
    FREELANCE(
        resourceKey = "category_freelance",
        descriptionKey = "category_freelance_desc",
        color = "#8BC34A",
        icon =
            "https://img.icons8.com/external-wanicon-flat-wanicon/64/" +
                "external-freelance-stay-at-home-wanicon-flat-wanicon.png",
    ),
    INVESTMENTS(
        resourceKey = "category_investments",
        descriptionKey = "category_investments_desc",
        color = "#009688",
        icon = "https://img.icons8.com/color/512/investment-portfolio.png",
    ),
    GIFT(
        resourceKey = "category_gift",
        descriptionKey = "category_gift_desc",
        color = "#FF9800",
        icon = "https://img.icons8.com/color/512/gift.png",
    ),

    // Expense Categories
    GROCERIES(
        resourceKey = "category_groceries",
        descriptionKey = "category_groceries_desc",
        color = "#4CAF50",
        icon = "https://img.icons8.com/color/512/shopping-cart.png",
    ),
    TRANSPORTATION(
        resourceKey = "category_transportation",
        descriptionKey = "category_transportation_desc",
        color = "#2196F3",
        icon = "https://img.icons8.com/color/512/car.png",
    ),
    UTILITIES(
        resourceKey = "category_utilities",
        descriptionKey = "category_utilities_desc",
        color = "#FF9800",
        icon = "https://img.icons8.com/color/512/light-on.png",
    ),
    DINING(
        resourceKey = "category_dining",
        descriptionKey = "category_dining_desc",
        color = "#F44336",
        icon = "https://img.icons8.com/color/512/restaurant.png",
    ),
    ENTERTAINMENT(
        resourceKey = "category_entertainment",
        descriptionKey = "category_entertainment_desc",
        color = "#9C27B0",
        icon = "https://img.icons8.com/color/512/cinema.png",
    ),
    HEALTHCARE(
        resourceKey = "category_healthcare",
        descriptionKey = "category_healthcare_desc",
        color = "#E91E63",
        icon = "https://img.icons8.com/color/512/health.png",
    ),
    EDUCATION(
        resourceKey = "category_education",
        descriptionKey = "category_education_desc",
        color = "#3F51B5",
        icon = "https://img.icons8.com/color/512/education.png",
    ),
    SHOPPING(
        resourceKey = "category_shopping",
        descriptionKey = "category_shopping_desc",
        color = "#FF5722",
        icon = "https://img.icons8.com/color/512/shopping-bag.png",
    ),
    SUBSCRIPTIONS(
        resourceKey = "category_subscriptions",
        descriptionKey = "category_subscriptions_desc",
        color = "#607D8B",
        icon = "https://img.icons8.com/color/512/subscription.png",
    ),
    RENT(
        resourceKey = "category_rent",
        descriptionKey = "category_rent_desc",
        color = "#795548",
        icon = "https://img.icons8.com/color/512/home.png",
    ),
    INSURANCE(
        resourceKey = "category_insurance",
        descriptionKey = "category_insurance_desc",
        color = "#00BCD4",
        icon = "https://img.icons8.com/color/512/insurance.png",
    ),
    OTHER(
        resourceKey = "category_other",
        descriptionKey = "category_other_desc",
        color = "#9E9E9E",
        icon = "https://img.icons8.com/color/512/more.png",
    ),
    ;

    /**
     * Convert default category to domain Category model
     * Uses resource keys for name and description (will be localized in UI layer)
     */
    fun toCategory(): Category =
        Category(
            id = 0, // Will be auto-assigned by database
            name = resourceKey, // Store key, not translated text
            description = descriptionKey,
            color = color,
            icon = icon,
            isDefault = true,
            resourceKey = resourceKey,
        )

    companion object {
        /**
         * Get all default categories as domain Category models
         */
        fun getAllCategories(): List<Category> = values().map { it.toCategory() }
    }
}
