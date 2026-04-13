# Pages and Dialogs Documentation

This document provides a comprehensive overview of all pages and dialogs in the Masarify application.

## Table of Contents

### Pages
1. [Splash Page](#1-splash-page)
2. [Onboarding Page](#2-onboarding-page)
3. [Bank Accounts Page](#3-bank-accounts-page)
4. [Transactions Page](#4-transactions-page)
5. [Currencies Page](#5-currencies-page)
6. [Categories Page](#6-categories-page)
7. [Add/Edit Category Page](#7-addedit-category-page)
8. [Create Bank Account Page](#8-create-bank-account-page)
9. [More Page](#9-more-page)
10. [Delete Bank Account Page](#10-delete-bank-account-page)
11. [Delete Category Page](#11-delete-category-page)

### Dialogs
1. [AddEditCurrencyDialog](#1-addeditcurrencydialog)
2. [AppInputAlertDialog](#2-appinputalertdialog)
3. [AppAlertDialog](#3-appalertdialog)
4. [ColorPickerDialog](#4-colorpickerdialog)
5. [AdvancedFilterDialog](#5-advancedfilterdialog)
6. [ImageZoomDialog](#6-imagezoomdialog)
7. [AddEditTransactionDialog](#7-addedittransactiondialog)

---

## Pages

### 1. Splash Page

**File Location**: `composeApp/src/commonMain/kotlin/com/lightfeather/masarify/page/splash/SplashPage.kt`

**Main Composables**:
- `SplashPage()` - Entry composable with ViewModel integration
- `SplashPageContent()` - UI composable

**State Class**: `SplashPageState`
- Currently empty placeholder for future loading states

**Intent Class**: `SplashPageIntent` (sealed interface)
- `NavigateToStart` - Navigate from splash screen to main app or onboarding

**Use Case**:
Initial landing screen displayed when the app launches. Acts as a transition screen while the app initializes and determines whether to show onboarding or the main dashboard.

**Key Features**:
- Minimal UI with app branding
- Automatic navigation after initialization
- Determines user's first-time status

---

### 2. Onboarding Page

**File Location**: `composeApp/src/commonMain/kotlin/com/lightfeather/masarify/page/onboarding/OnBoardingPage.kt`

**Main Composables**:
- `OnBoardingPage()` - Entry composable with ViewModel integration
- `OnBoardingPageContent()` - Form UI with validation

**State Class**: `OnBoardingPageState`
- `userName: String` - User's full name
- `accountName: String` - Initial account name
- `accountBalance: String` - Starting balance
- `selectedCurrency: UiCurrency?` - Selected currency
- `appCurrencies: List<UiCurrency>` - Available currencies
- `accountColor: String` - Account color hex
- `accountLogo: String` - Account logo URL
- `selectedBank: UiBankName?` - Selected bank (sets logo)
- `availableBanks: List<UiBankName>` - Available bank names

**Intent Class**: `OnBoardingPageIntent` (sealed interface)
- `UpdateUserName(name: String)` - Update user name field
- `UpdateAccountName(name: String)` - Update account name field
- `UpdateCurrency(currency: UiCurrency)` - Select currency
- `UpdateAccountBalance(balance: String)` - Update balance field
- `SelectBank(bank: UiBankName)` - Select bank (auto-fills logo)
- `AddNewBank(bankName: String)` - Create and select a custom bank
- `AddNewCurrency(currency: UiCurrency)` - Create custom currency
- `Submit` - Submit onboarding form (marks account as default)

**Use Case**:
First-time user setup wizard that collects essential information to initialize the app. Creates the user profile and their first bank account, which is automatically set as the **default account**.

**Key Features**:
- Background image with app logo
- Multi-field form with validation
- Required fields: User name, Account name, Initial balance, Currency
- Bank selection dropdown with search (includes Cash + all built-in banks)
- "Add New Bank" option in dropdown for custom banks
- Selecting a bank auto-fills the account logo
- Currency dropdown with search and "Add New Currency" option
- First account created is automatically marked as default
- Submit button with loading state

---

### 3. Bank Accounts Page

**File Location**: `composeApp/src/commonMain/kotlin/com/lightfeather/masarify/page/bankaccounts/BankAccountsPage.kt`

**Main Composables**:
- `BankAccountsPage()` - Entry composable with ViewModel and Navigator
- `BankAccountsPageContent()` - Adaptive UI with ListDetailPaneScaffold
- `AccountsListPane()` - List pane showing accounts with wealth summary

**State Class**: `BankAccountsPageState`
- `bankAccounts: Flow<List<UiBankAccount>>` - All bank accounts
- `userAccountsCurrencies: Flow<List<UiCurrency>>` - Available currencies
- `defaultCurrency: Flow<UiCurrency?>` - User's default currency
- `selectedAccount: UiBankAccount?` - Currently selected account
- `selectedCurrency: UiCurrency?` - Currency for wealth display
- `totalAmountInSelectedOrDefaultCurrency: String` - Total wealth
- `showAddEditDialog: Boolean` - Dialog visibility state
- Transaction-related state properties
- `transactionAttachments: Map<String, List<UiAttachment>>` - Transaction attachments

**Intent Class**: `BankAccountsPageIntent` (sealed interface)
- `LoadData` - Initialize page data
- `SelectAccount(account: UiBankAccount)` - Select account for detail view
- `SelectCurrency(currency: UiCurrency?)` - Change wealth display currency
- `DeleteBankAccount(account: UiBankAccount)` - Delete account
- `CreateTransactionInAccount(account: UiBankAccount)` - Create transaction
- `TransferFromAccount(account: UiBankAccount)` - Initiate transfer
- `UpdateTransaction(transaction: UiTransactionDetails)` - Edit transaction
- `DeleteTransaction(transaction: UiTransactionDetails)` - Delete transaction
- `DuplicateTransaction(transaction: UiTransactionDetails)` - Copy transaction
- `PickImages` - Open image picker
- `DeleteAttachment(attachment: UiAttachment)` - Remove attachment
- Dialog management intents

**Detail Panes**:
1. **AddBankAccount** - Create new bank account
2. **ViewBankAccount** - Display account transactions using `TransactionsPane`
3. **ViewTransaction** - Show transaction details with actions
4. **UpdateBankAccount** - Edit existing account

**Use Case**:
Central hub for managing all bank accounts. Provides overview of total wealth across accounts and allows users to view/edit accounts and their transactions.

**Key Features**:
- Adaptive two/three-pane layout (mobile/tablet/desktop)
- Wealth summary header with currency selector
- Account list with balance indicators
- Account actions: Add transaction, Transfer, Edit, Delete
- Account detail pane showing transactions
- Transaction detail pane with edit/delete/duplicate
- FAB for adding new accounts
- Animated selection states
- BackHandler for proper navigation

---

### 4. Transactions Page

**File Location**: `composeApp/src/commonMain/kotlin/com/lightfeather/masarify/page/transactions/TransactionsPage.kt`

**Main Composables**:
- `TransactionsPage()` - Entry composable with optional pre-configuration
- `TransactionsPageContent()` - Adaptive UI with ListDetailPaneScaffold
- `TransactionDetailPane()` - Detail pane wrapper for transaction view

**Entry Parameters**:
- `openAddDialog: Boolean = false` - Auto-open add dialog
- `transactionType: UiTransactionType? = null` - Pre-select type (INCOME/EXPENSE)
- `fromAccountId: String? = null` - Pre-select source account

**State Class**: `TransactionsPageState`
- `filter: UiTransactionFilter` - Active transaction filter
- `accounts: Flow<List<UiBankAccount>>` - Available accounts
- `categories: Flow<List<UiCategory>>` - Available categories
- `currencies: Flow<List<UiCurrency>>` - Available currencies
- `userAccountsCurrencies: Flow<List<UiCurrency>>` - User currencies
- `defaultCurrency: Flow<UiCurrency?>` - Default currency
- `selectedCurrency: UiCurrency?` - Currency for total display
- `totalAmountInSelectedOrDefaultCurrency: String` - Filtered total
- `showFilterDialog: Boolean` - Filter dialog visibility
- `showAddEditDialog: Boolean` - Add/Edit dialog visibility
- `editingTransaction: UiTransactionDetails?` - Transaction being edited
- `lockedFromAccount: UiBankAccount?` - Locked source account (for transfers)
- `defaultAccount: UiBankAccount?` - Default account for pre-selection in add dialog
- `transactionAttachments: Map<String, List<UiAttachment>>` - Attachments
- `selectedAttachments: List<UiAttachment>` - Selected for viewing

**Intent Class**: `TransactionsPageIntent` (sealed interface)
- `LoadData` - Initialize page data
- `SelectCurrency(currency: UiCurrency?)` - Change display currency
- `ShowFilterDialog` / `HideFilterDialog` - Toggle filter dialog
- `UpdateFilter(filter: UiTransactionFilter)` - Apply filter
- `SaveFilter(name: String, filter: UiTransactionFilter)` - Save filter preset
- `ShowAddDialog` - Open add transaction dialog
- `ShowAddDialogWithType(type: UiTransactionType, fromAccountId: String?)` - Open with pre-selection
- `HideAddEditDialog` - Close dialog
- `CreateTransaction(data: UiTransactionDetails)` - Create transaction
- `ShowEditDialog(transaction: UiTransactionDetails)` - Open edit dialog
- `UpdateTransaction(data: UiTransactionDetails)` - Update transaction
- `DeleteTransaction(transaction: UiTransactionDetails)` - Delete transaction
- `DuplicateTransaction(transaction: UiTransactionDetails)` - Copy transaction
- `PickImages` - Open image picker
- `DeleteAttachment(attachment: UiAttachment)` - Remove attachment

**Use Case**:
Comprehensive transaction management interface. Allows users to view, filter, add, edit, and delete all financial transactions (income, expenses, transfers).

**Key Features**:
- Adaptive list-detail layout
- Wealth summary header with currency selector
- Advanced filtering system with badge counter
- Filter dialog with multiple criteria
- Add/Edit transaction dialog with type tabs
- Transaction list with category indicators
- Transaction detail view with actions
- Image attachment support
- Transaction duplication feature
- Pre-configured entry (e.g., from account page)
- Default account pre-selected when opening add dialog (user can change)
- Filter presets (save/load)

---

### 5. Currencies Page

**File Location**: `composeApp/src/commonMain/kotlin/com/lightfeather/masarify/page/currencies/CurrenciesPage.kt`

**Main Composables**:
- `CurrenciesPage()` - Entry composable with ViewModel
- `CurrenciesPageContent()` - UI with currency management
- `BaseCurrencySelector()` - Dropdown for base currency
- `ExchangeRatesSection()` - Exchange rates list with edit mode
- `ExchangeRateItem()` - Individual rate item with input

**State Class**: `CurrenciesPageState`
- `allCurrencies: Flow<List<UiCurrency>>` - All available currencies
- `exchangeRates: Flow<List<UiCurrencyExchangeRate>>` - Exchange rates
- `baseCurrency: UiCurrency?` - Selected base currency
- `isEditMode: Boolean` - Edit mode toggle
- `showAddEditDialog: Boolean` - Add/Edit dialog visibility
- `editingCurrency: UiCurrency?` - Currency being edited
- `showDeleteDialog: Boolean` - Delete confirmation visibility
- `deletingCurrency: UiCurrency?` - Currency being deleted

**Intent Class**: `CurrenciesPageIntent` (sealed interface)
- `LoadData` - Initialize page data
- `SelectBaseCurrency(currency: UiCurrency)` - Change base currency
- `ShowAddCurrencyDialog(show: Boolean)` - Toggle add dialog
- `ShowEditCurrencyDialog(currency: UiCurrency)` - Open edit dialog
- `ShowDeleteDialog(currency: UiCurrency)` - Show delete confirmation
- `HideDialogs` - Close all dialogs
- `CreateCurrency(name: String, symbol: String)` - Create currency
- `UpdateCurrency(currency: UiCurrency?, name: String, symbol: String)` - Edit currency
- `DeleteCurrency(currency: UiCurrency)` - Delete currency
- `ToggleEditMode(isEditing: Boolean)` - Switch edit/view mode
- `SaveExchangeRates(rates: List<UiCurrencyExchangeRate>)` - Save all rates

**Use Case**:
Manage currencies and exchange rates for multi-currency account support. Allows users to add custom currencies and maintain exchange rates.

**Key Features**:
- Base currency selector dropdown
- Exchange rates list (view/edit modes)
- Edit mode with text input for rates
- "Update inverse exchange rate" checkbox
- Add currency dialog (name + symbol)
- Edit currency functionality
- Delete currency with confirmation
- Save all rates button in edit mode
- FAB for adding currencies

---

### 6. Categories Page

**File Location**: `composeApp/src/commonMain/kotlin/com/lightfeather/masarify/page/categories/CategoriesPage.kt`

**Main Composables**:
- `CategoriesPage()` - Entry composable with ViewModel and Navigator
- `CategoriesPageContent()` - Adaptive UI with ListDetailPaneScaffold
- `CategoriesListPane()` - List pane showing all categories

**State Class**: `CategoriesPageState`
- `categories: Flow<List<UiCategory>>` - All expense/income categories

**Intent Class**: `CategoriesPageIntent` (sealed interface)
- `LoadData` - Initialize page data
- `DeleteCategory(category: UiCategory)` - Delete category

**Detail Panes**:
1. **AddEditCategory** - Create or edit category

**Use Case**:
Manage expense and income categories for transaction organization. Users can create custom categories with colors and icons.

**Key Features**:
- Adaptive list-detail layout
- Category list with color/icon preview
- Delete category functionality
- FAB for adding categories
- Detail pane with category form
- BackHandler for navigation

---

### 7. Add/Edit Category Page

**File Location**: `composeApp/src/commonMain/kotlin/com/lightfeather/masarify/page/categories/addedit/AddEditCategoryPage.kt`

**Main Composables**:
- `AddEditCategoryPage()` - Entry composable
- `AddEditCategoryPageContent()` - Form UI
- `ColorPickerWithIcons()` - Color and icon selection
- `RecentColorsSection()` - Recently used colors grid
- `IconGridItem()` - Icon grid item
- `CategoryPreview()` - Live category preview

**State Class**: `AddEditCategoryPageState`
- `name: String` - Category name
- `description: String` - Category description
- `customIconUrl: String` - Custom icon URL
- `selectedColor: String` - Selected color hex
- `selectedIcon: Any?` - Selected icon reference
- `availableIcons: List<Any>` - Icon grid items
- `isLoadingIcons: Boolean` - Icon loading state
- `recentColors: List<String>` - Recently used colors
- `showColorPicker: Boolean` - Color picker dialog visibility
- `isEditMode: Boolean` - Create vs edit mode
- `isLoading: Boolean` - Save operation state
- `isValid: Boolean` - Form validation state

**Intent Class**: `AddEditCategoryPageIntent` (sealed interface)
- `UpdateName(name: String)` - Update name field
- `UpdateDescription(description: String)` - Update description
- `UpdateCustomIconUrl(url: String)` - Set custom icon URL
- `SelectColor(color: String)` - Select color
- `SelectIcon(iconUrl: Any)` - Select icon
- `ToggleColorPicker(show: Boolean)` - Show/hide color picker
- `SaveRecentColor(color: String)` - Add to recent colors
- `Cancel` - Cancel operation
- `Save` - Save category

**Use Case**:
Create or edit transaction categories with visual customization. Categories help organize transactions for better financial tracking.

**Key Features**:
- Name and description text fields
- Color picker dialog (HSV picker + recent colors)
- Icon grid selector with predefined icons
- Custom icon URL input
- Live category preview
- Responsive layout (compact/expanded)
- Form validation
- Save/Cancel buttons
- TopAppBar with back button

---

### 8. Create Bank Account Page

**File Location**: `composeApp/src/commonMain/kotlin/com/lightfeather/masarify/page/createbankaccount/CreateBankAccountPage.kt`

**Main Composables**:
- `CreateBankAccountPage()` - Entry composable
- `CreateBankAccountPageContent()` - Form UI

**Entry Parameters**:
- `account: UiBankAccount` - Account to edit (empty for create mode)
- `onBack: () -> Unit` - Back navigation callback

**State Class**: `CreateBankAccountPageState`
- `name: String` - Account name
- `description: String` - Account description
- `initialBalance: String` - Starting balance (or current for edit)
- `currency: UiCurrency?` - Account currency
- `color: String` - Account color hex
- `savedColors: List<String>` - Recently used colors
- `availableCurrencies: List<UiCurrency>` - Currency options
- `selectedBank: UiBankName?` - Selected bank (sets logo)
- `availableBanks: List<UiBankName>` - Available bank names
- `isDefault: Boolean` - Whether account is the default account
- `inEditMode: Boolean` - Create vs edit mode
- `isLoading: Boolean` - Save operation state

**Intent Class**: `CreateBankAccountPageIntent` (sealed interface)
- `UpdateName(name: String)` - Update name field
- `UpdateDescription(description: String)` - Update description
- `UpdateInitialBalance(balance: String)` - Update balance
- `UpdateCurrency(currency: UiCurrency)` - Select currency
- `UpdateColor(color: String)` - Select color
- `SaveColor(color: String)` - Add to saved colors
- `SelectBank(bank: UiBankName)` - Select bank (auto-fills logo)
- `AddNewBank(bankName: String)` - Create and select a custom bank
- `ToggleDefault` - Toggle default account status
- `AddNewCurrency(currency: UiCurrency)` - Create currency from dialog
- `NavigateBack` - Navigate back
- `Submit` - Create/update account

**Use Case**:
Create new bank accounts or edit existing ones. Accounts represent financial accounts that hold money (checking, savings, cash, credit cards, etc.).

**Key Features**:
- Account name text field (required)
- Description text field (optional)
- Bank selection dropdown with search (includes Cash + all built-in banks)
- "Add New Bank" option in dropdown for custom banks
- Selecting a bank auto-fills the account logo
- Balance input (numeric keyboard)
- Currency dropdown with search
- "Add New Currency" option in dropdown
- Color picker with saved colors grid
- "Set as default account" toggle switch
- Form validation
- Different labels for create vs edit mode
- Submit button with loading state
- TopAppBar with back button

---

### 9. More Page

**File Location**: `composeApp/src/commonMain/kotlin/com/lightfeather/masarify/page/more/MorePage.kt`

**Main Composables**:
- `MorePage()` - Entry composable with ViewModel and Navigator
- `MorePageContent()` - Adaptive UI with ListDetailPaneScaffold
- `MoreListPane()` - List pane with settings/options
- `CurrencyManagementDetailPane()` - Embeds CurrenciesPage
- `CategoryManagementDetailPane()` - Embeds CategoriesPage
- `PrivacyPolicyDetailPane()` - Privacy policy content
- `ContactUsDetailPane()` - Contact information
- `RateUsDetailPane()` - App rating prompt
- `DetailPaneWrapper()` - Wrapper for detail panes
- `SectionHeader()` - Section divider header

**State Class**: `MorePageState`
- `isDarkTheme: Boolean` - Dark mode toggle state
- `selectedLanguage: AppLanguage` - Current app language
- `availableLanguages: List<AppLanguage>` - Language options

**Intent Class**: `MorePageIntent` (sealed interface)
- `LoadData` - Initialize page data
- `ToggleDarkTheme(enabled: Boolean)` - Toggle dark mode
- `SelectLanguage(language: AppLanguage)` - Change app language
- `ClearNavigation` - Clear navigation stack
- Nested `NavigationIntent`:
  - `SelectCurrencyManagementDetail` - Navigate to currencies
  - `SelectCategoryManagementDetail` - Navigate to categories
  - `SelectPrivacyPolicyDetail` - Navigate to privacy
  - `SelectContactUsDetail` - Navigate to contact
  - `SelectRateUsDetail` - Navigate to rating

**Navigation Destinations** (enum `MoreNavDestination`):
- CURRENCY_MANAGEMENT
- CATEGORY_MANAGEMENT
- PRIVACY_POLICY
- CONTACT_US
- RATE_US

**Use Case**:
Settings and management hub providing access to app preferences, currency/category management, and support resources.

**Key Features**:
- Adaptive list-detail layout
- Dark theme toggle switch
- Language selector dropdown
- Section headers (Settings, App Management, Support)
- Navigation to currency management
- Navigation to category management
- Privacy policy viewer
- Contact us information
- Rate us prompt
- BackHandler for detail pane navigation

---

### 10. Delete Bank Account Page

**File Location**: `composeApp/src/commonMain/kotlin/com/lightfeather/masarify/page/deletebankaccount/DeleteBankAccountPage.kt`

**Main Composables**:
- `DeleteBankAccountPage()` - Entry composable
- `DeleteBankAccountPageContent()` - Alert dialog

**Use Case**:
Confirmation dialog for deleting a bank account. Prevents accidental deletion of financial data.

**Key Features**:
- Alert dialog UI
- Shows account name in confirmation message
- Confirm/Cancel buttons
- Destructive action confirmation

---

### 11. Delete Category Page

**File Location**: `composeApp/src/commonMain/kotlin/com/lightfeather/masarify/page/deletecategory/DeleteCategoryPage.kt`

**Main Composables**:
- `DeleteCategoryPage()` - Entry composable
- `DeleteCategoryPageContent()` - Alert dialog

**Use Case**:
Confirmation dialog for deleting a category. Prevents accidental removal of category organization.

**Key Features**:
- Alert dialog UI
- Shows category name in confirmation message
- Confirm/Cancel buttons
- Destructive action confirmation

---

## Dialogs

### 1. AddEditCurrencyDialog

**File Location**: `composeApp/src/commonMain/kotlin/com/lightfeather/masarify/page/currencies/AddEditCurrencyDialog.kt`

**Module**: composeApp

**Type**: Material3 AlertDialog

**Parameters**:
- `currency: UiCurrency?` - Currency to edit (null for create)
- `onDismiss: () -> Unit` - Dismiss callback
- `onConfirm: (name: String, symbol: String) -> Unit` - Confirm callback
- `modifier: Modifier = Modifier`

**Use Case**:
Create or edit currency definitions. Users can add custom currencies with name and symbol.

**Key Features**:
- Two text fields: Currency name, Currency symbol
- Form validation
- Cancel/Confirm buttons
- Auto-populated fields in edit mode

---

### 2. AppInputAlertDialog

**File Location**: `designsystem/src/commonMain/kotlin/com/lightfeather/designsystem/component/molecules/dialog/AppInputAlertDialog.kt`

**Module**: designsystem (Reusable)

**Type**: Material3 AlertDialog with text input

**Parameters**:
- `title: String` - Dialog title
- `message: String` - Dialog message/description
- `inputValue: String` - Current input value
- `onValueChange: (String) -> Unit` - Input change callback
- `onDismissRequest: () -> Unit` - Dismiss callback
- `onConfirm: (String) -> Unit` - Confirm callback with input value
- `modifier: Modifier = Modifier`
- `inputLabel: String = ""` - Optional input field label
- `keyboardType: KeyboardType = KeyboardType.Text` - Keyboard type

**Use Case**:
General-purpose dialog for collecting single text input from user. Used for save filter name, rename operations, etc.

**Key Features**:
- Title and message display
- Single text input field
- Customizable keyboard type
- Optional input label
- Cancel/Confirm buttons
- Returns input value on confirm

---

### 3. AppAlertDialog

**File Location**: `designsystem/src/commonMain/kotlin/com/lightfeather/designsystem/component/molecules/dialog/AppAlertDialog.kt`

**Module**: designsystem (Reusable)

**Type**: Material3 AlertDialog

**Parameters**:
- `title: String` - Dialog title
- `message: String` - Dialog message
- `onDismissRequest: () -> Unit` - Dismiss callback
- `onConfirm: () -> Unit` - Confirm callback
- `modifier: Modifier = Modifier`

**Use Case**:
Simple confirmation dialog for yes/no decisions. Used for delete confirmations, important warnings, etc.

**Key Features**:
- Title and message display
- Yes/Cancel buttons
- Dismissible with outside tap
- Material3 styling

---

### 4. ColorPickerDialog

**File Location**: `designsystem/src/commonMain/kotlin/com/lightfeather/designsystem/component/molecules/dialog/ColorPickerDialog.kt`

**Module**: designsystem (Reusable)

**Type**: Custom Dialog with Card wrapper

**Parameters**:
- `initialColor: Color = Color.Magenta` - Starting color
- `savedColors: List<String> = emptyList()` - Saved color hex strings
- `onColorChange: (Color) -> Unit = {}` - Real-time color change
- `onRgbaChange: (Int, Int, Int, Float) -> Unit` - RGBA values callback
- `onSavedColorClick: (String) -> Unit = {}` - Saved color selection
- `onDismiss: () -> Unit = {}` - Dismiss callback
- `onConfirm: (Color) -> Unit = {}` - Confirm callback with final color

**Helper Composables**:
- `HSVColorPicker` - Main picker component
- `SaturationValuePicker` - 2D saturation/value canvas
- `HueSlider` - Hue selection slider
- `AlphaSlider` - Opacity slider
- `ColorPreviewWithHex` - Preview box with hex input
- `SavedColorsGrid` - Saved colors grid
- `SinWaveColorLayout` - Flow layout for color circles

**Use Case**:
Advanced color selection for account colors, category colors, etc. Provides precise color control with multiple input methods.

**Key Features**:
- HSV color model picker
- 2D saturation/value canvas picker
- Hue slider (0-360°)
- Alpha/opacity slider
- Hex color input field
- Color preview box
- Saved colors quick selection grid
- Real-time color updates
- RGBA value callbacks
- Confirm/Cancel buttons

---

### 5. AdvancedFilterDialog

**File Location**: `designsystem/src/commonMain/kotlin/com/lightfeather/designsystem/component/organisms/dialog/AdvancedFilterDialog.kt`

**Module**: designsystem (Reusable)

**Type**: Material3 BasicAlertDialog with Card wrapper

**Parameters**:
- `filter: UiTransactionFilter` - Current filter state
- `accounts: List<UiBankAccount>` - Available accounts for filtering
- `categories: List<UiCategory>` - Available categories
- `currencies: List<UiCurrency>` - Available currencies
- `onDismiss: () -> Unit` - Dismiss callback
- `onApply: (UiTransactionFilter) -> Unit` - Apply filter callback
- `onSave: ((String, UiTransactionFilter) -> Unit)? = null` - Save filter preset (optional)
- `modifier: Modifier = Modifier`

**Helper Composables**:
- `FilterSection` - Reusable section header/content wrapper

**Nested Dialog**: Save filter dialog for naming filter presets

**Use Case**:
Comprehensive transaction filtering interface. Allows users to filter transactions by multiple criteria for detailed financial analysis.

**Key Features**:
- Text search field (transaction name)
- Transaction type chips (Income, Expense, Transfer)
- Account multi-select dropdown
- Category multi-select dropdown
- Currency multi-select dropdown
- Amount range inputs (min/max)
- Attachments filter (radio buttons: All, With, Without)
- Filter logic toggle (AND/OR for multiple criteria)
- Save filter preset functionality
- Reset filter button
- Apply/Cancel buttons
- Scrollable content

---

### 6. ImageZoomDialog

**File Location**: `designsystem/src/commonMain/kotlin/com/lightfeather/designsystem/component/molecules/ImageZoomDialog.kt`

**Module**: designsystem (Reusable)

**Type**: Custom full-screen Dialog

**Parameters**:
- `attachments: List<UiAttachment>` - Images to display
- `initialPage: Int` - Starting image index
- `onDismiss: () -> Unit` - Dismiss callback
- `modifier: Modifier = Modifier`

**Helper Composables**:
- `ZoomableImage` - Individual zoomable image with gestures

**Use Case**:
Full-screen image viewer for transaction attachments (receipts, documents, photos). Provides detailed image inspection.

**Key Features**:
- Full-screen immersive view
- Horizontal pager for multiple images
- Pinch-to-zoom gesture support
- Pan/drag gestures when zoomed
- Double-tap to zoom in/out
- Image counter (e.g., "1 / 3")
- Close button
- Smooth transitions between images
- Reset zoom on page change

---

### 7. AddEditTransactionDialog

**File Location**: `designsystem/src/commonMain/kotlin/com/lightfeather/designsystem/component/organisms/dialog/AddEditTransactionDialog.kt`

**Module**: designsystem (Reusable)

**Type**: Material3 BasicAlertDialog with Card wrapper

**Parameters**:
- `transaction: UiTransactionDetails?` - Transaction to edit (null for create)
- `initialType: UiTransactionType = UiTransactionType.EXPENSE` - Default type
- `lockedFromAccount: UiBankAccount? = null` - Locked source account (user cannot change)
- `initialAccount: UiBankAccount? = null` - Pre-selected account (user can change)
- `initialCategory: UiCategory? = null` - Pre-selected category
- `accounts: List<UiBankAccount>` - Available accounts
- `categories: List<UiCategory>` - Available categories
- `attachments: List<UiAttachment> = emptyList()` - Current attachments
- `onDismiss: () -> Unit` - Dismiss callback
- `onSave: (UiTransactionData) -> Unit` - Save callback
- `onPickImages: () -> Unit` - Image picker callback
- `onDeleteAttachment: (UiAttachment) -> Unit` - Delete attachment callback
- `modifier: Modifier = Modifier`

**Helper Composables**:
- `ExpenseFields` - Expense-specific form fields
- `IncomeFields` - Income-specific form fields
- `TransferFields` - Transfer-specific form fields

**Data Class**: `UiTransactionData` - Form submission data

**Use Case**:
Comprehensive dialog for creating and editing financial transactions. Supports three transaction types: expenses, income, and transfers between accounts.

**Key Features**:
- Tab-based type selection (Expense/Income/Transfer)
- Common fields: Transaction name, Amount, Description
- Type-specific fields:
  - **Expense**: Account, Category, Multiple categories support
  - **Income**: Account, Income source/category
  - **Transfer**: From account, To account, Transfer fee
- Image attachment support (thumbnails with delete)
- Add attachments button
- Form validation before save
- Pre-populated fields in edit mode
- Locked account support (e.g., from account page — cannot be changed)
- Default account pre-selection support (user can change)
- Scrollable content
- Save/Cancel buttons

---

## Page & Dialog Statistics

### Pages Summary
- **Total Pages**: 11 main pages
- **Adaptive Pages** (ListDetailPaneScaffold): 4 (BankAccounts, Transactions, Categories, More)
- **Form Pages**: 3 (Onboarding, CreateBankAccount, AddEditCategory)
- **Management Pages**: 1 (Currencies)
- **Navigation Pages**: 1 (Splash)
- **Dialog Pages**: 2 (DeleteBankAccount, DeleteCategory)

### Dialogs Summary
- **Total Dialogs**: 7
- **designsystem Module**: 6 (reusable)
- **composeApp Module**: 1 (feature-specific)
- **Dialog Types**:
  - Simple Alerts: 1 (AppAlertDialog)
  - Input Dialogs: 2 (AppInputAlertDialog, AddEditCurrencyDialog)
  - Complex Dialogs: 2 (AdvancedFilterDialog, AddEditTransactionDialog)
  - Utility Dialogs: 2 (ColorPickerDialog, ImageZoomDialog)

### Architecture Patterns Used
- **MVI Pattern**: All pages use State, Intent, ViewModel architecture
- **Material3 Adaptive**: ListDetailPaneScaffold for responsive layouts
- **Flow-based State**: Reactive data streams with Kotlin Flow
- **Form Validation**: Client-side validation with error messages
- **BackHandler**: Proper back navigation in adaptive layouts
- **String Resources**: All user-facing text uses MR.strings
- **SnackbarService**: Error/success messages via centralized service
- **Design Tokens**: AppTheme.dimens for consistent spacing

---

## Navigation Flow

```
Splash Page
    ↓
Onboarding Page (first-time users)
    ↓
Dashboard (via NavigationSuite)
    ├── Bank Accounts Page
    │   ├── Detail: View Bank Account (TransactionsPane)
    │   ├── Detail: Add Bank Account (CreateBankAccountPage)
    │   ├── Detail: Update Bank Account (CreateBankAccountPage)
    │   └── Detail: View Transaction (TransactionDetailView)
    ├── Transactions Page
    │   └── Detail: View Transaction (TransactionDetailView)
    ├── Categories Page
    │   └── Detail: Add/Edit Category (AddEditCategoryPage)
    └── More Page
        ├── Detail: Currency Management (CurrenciesPage)
        ├── Detail: Category Management (CategoriesPage)
        ├── Detail: Privacy Policy
        ├── Detail: Contact Us
        └── Detail: Rate Us
```

---

## Key Design Decisions

1. **Adaptive Navigation**: Material3 ListDetailPaneScaffold provides seamless phone/tablet/desktop experience
2. **Centralized Dialogs**: Most dialogs in designsystem for reusability across features
3. **Type Safety**: Sealed interfaces for intents ensure compile-time safety
4. **Separation of Concerns**: State, Intent, ViewModel, UI clearly separated
5. **Accessibility**: ContentDescription, semantic structure, proper focus handling
6. **Localization**: Full RTL support, Arabic/English via moko-resources
7. **Error Handling**: SnackbarService for user feedback, no error state in models
8. **Platform Parity**: All pages/dialogs work across Android, iOS, Web, Desktop

---

*Last Updated: 2026-03-23*
