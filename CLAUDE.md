# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Masarify is a Kotlin Multiplatform project targeting Android, iOS, Web (WASM), and Desktop (JVM) platforms. It's a financial management application built with Compose Multiplatform and follows clean architecture patterns with MVI for the presentation layer.

## Essential Commands

### Build and Run
- **Web (WASM) Development**: `./gradlew :composeApp:wasmJsBrowserDevelopmentRun`
- **Android**: `./gradlew assembleDebug` or run from Android Studio
- **Desktop (JVM)**: `./gradlew run` or `./gradlew :composeApp:runDistributable`
- **iOS**: Open `iosApp` in Xcode and build
- **Clean Build**: `./gradlew clean`

### Development Workflow
- **Run All Tests**: `./gradlew check`
- **Generate Resources**: Resources are auto-generated during build via moko-resources
- **SQLDelight Schema**: Schema files generate Kotlin code automatically during build

### Code Quality & Linting ✅
- **Format Code (KtLint)**: `./gradlew ktlintFormat`
- **Check Code Style**: `./gradlew ktlintCheck` ✅ **WORKING**
- **Static Analysis (DetektKT)**: `./gradlew detekt` ✅ **WORKING**
- **Run All Quality Checks**: `./gradlew ktlintCheck detekt` ✅ **WORKING**
- **Pre-commit Quality Gate**: `./gradlew ktlintFormat ktlintCheck detekt` ✅ **WORKING**
- **Generate DetektKT Baseline**: `./gradlew detektBaseline` (for existing codebases)
- **IDE Integration**: Install KtLint and DetektKT plugins for real-time feedback

### Verification Commands
- **Check KtLint Exclusions**: `./gradlew ktlintCheck --info | grep "Scanning"`
- **List Generated Files**: `find . -name "*.kt" -path "*/build/*" | head -10`
- **Verify No Build Files Scanned**: KtLint should only scan `src/` directories
- **Test Clean Build**: `./gradlew clean && ./gradlew ktlintCheck` (should pass)
- **Debug KtLint Sources**: `./gradlew ktlintCheck --debug | grep -E "(Scanning|Checking)"`
- **Check Build Status**: `ls -la */build/generated/` (should show generated files exist)

### KtLint & DetektKT Status ✅ FULLY CONFIGURED
**Current Configuration:**
- ✅ **KtLint 1.4.1** with automatic code formatting
- ✅ **DetektKT 1.24.0** with static analysis and formatting plugins  
- ✅ **Generated files excluded** via triple-layer exclusion system:
  - Root-level global configuration with `afterEvaluate` task disabling
  - Comprehensive `.ktlintignore` file with all generated file patterns
  - Module-specific `detekt.yml` configuration
- ✅ **All main source sets properly excluded** from generated file scanning
- ✅ **All quality checks passing** without false positives
- ✅ **Code quality foundation complete** and ready for development

**Generated File Exclusions Working For:**
- SQLDelight generated code (`*Queries.kt`, `Database.kt`)
- Moko resources (`MR.kt`, resource collectors/accessors)
- Compose resources (resource generators, collectors)
- Kotlin compiler generated files
- Platform-specific build artifacts

**Troubleshooting (Historical - Issues Resolved):**
- ~~SQLDelight files still scanned~~ ✅ **FIXED**
- ~~Generated files being scanned~~ ✅ **FIXED**
- ~~Build file violations~~ ✅ **FIXED**

## Architecture Overview

### Module Structure (Clean Architecture)
```
├── composeApp/     # Main application module with UI and navigation
├── domain/         # Business logic, entities, and use cases (pure Kotlin)
├── data/          # Repositories, data sources, DTOs, and database
└── designsystem/  # Reusable UI components with platform parity
```

### Key Architectural Patterns

**MVI (Model-View-Intent) Pattern:**
- State: Immutable data classes (e.g., `TransactionPageState`)
- Intent: Sealed interfaces for user actions (e.g., `TransactionPageIntent`)
- ViewModels: Process intents and emit state via Flow
- UI: Stateless composables that receive state and intent handlers

**List-Detail Adaptive Pattern (CRITICAL - Always Follow):**
For pages with adaptive navigation (phone/tablet support), use Material3's `ListDetailPaneScaffold`:

**Required Components:**
1. **State**: Include `selectedItem` and related detail state
2. **Intent**: NavigationIntent sealed class with `ThreePaneScaffoldNavigator` parameter
3. **ViewModel**: Handle navigation lifecycle and clear state on back navigation
4. **UI**: Separate `ListPane` and `DetailPane` composables with `AnimatedPane` wrapper

**Implementation Pattern:**
```kotlin
// State - Include selected item for detail pane
data class FeaturePageState(
    val items: Flow<List<Item>>,
    val selectedItem: Item? = null,
    val selectedDetailType: DetailType? = null,
)

// Intent - NavigationIntent for detail pane navigation
sealed interface FeaturePageIntent {
    sealed class NavigationIntent(
        open val item: Item?,
        open val navigator: ThreePaneScaffoldNavigator<NavigationIntent>,
    ) : FeaturePageIntent

    data class ClearNavigation(
        val navigator: ThreePaneScaffoldNavigator<NavigationIntent>,
    ) : FeaturePageIntent
}

// ViewModel - Handle navigation state management
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
internal fun onIntent(intent: FeaturePageIntent) {
    when (intent) {
        is FeaturePageIntent.ClearNavigation -> {
            viewModelScope.launch {
                while (intent.navigator.canNavigateBack()) {
                    intent.navigator.navigateBack()
                }
                _state.value = _state.value.copy(selectedItem = null)
            }
        }
        is FeaturePageIntent.NavigationIntent -> {
            viewModelScope.launch {
                // Clear existing navigation
                while (intent.navigator.canNavigateBack()) {
                    intent.navigator.navigateBack()
                }
                _state.value = _state.value.copy(selectedItem = null)
                // Navigate to detail
                intent.navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, intent)
                _state.value = _state.value.copy(selectedItem = intent.item)
            }
        }
    }
}

// UI - Use ListDetailPaneScaffold with proper back handling
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun FeaturePageContent(state: State, onIntent: (Intent) -> Unit) {
    val navigator = rememberListDetailPaneScaffoldNavigator<NavigationIntent>()

    BackHandler(navigator.canNavigateBack()) {
        coroutineScope.launch {
            onIntent(FeaturePageIntent.ClearNavigation(navigator))
        }
    }

    ListDetailPaneScaffold(
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = { ListPaneContent(...) },
        detailPane = {
            when (val destination = navigator.currentDestination?.contentKey) {
                is NavigationIntent.SpecificDetail -> {
                    key("detail_${destination.item.id}") {
                        DetailPaneContent(...)
                    }
                }
                null -> EmptyState(...)
            }
        }
    )
}
```

**Critical Rules:**
- **Always use `key()` for detail pane content** to ensure proper recomposition
- **Clear navigation state first** before setting new navigation in ViewModel
- **Handle back navigation** with `BackHandler` and `ClearNavigation` intent
- **Use `AnimatedPane` wrapper** for list pane content
- **Include `selectedItem` in state** for visual feedback and detail pane content
- **Reference**: See `BankAccountsPage.kt` for complete implementation example

**Platform Targets:**
- `commonMain`: Shared code across all platforms
- `androidMain`: Android-specific implementations
- `iosMain`: iOS-specific implementations  
- `jvmMain`: Desktop-specific implementations
- `wasmJsMain`: Web-specific implementations

### Key Technologies
- **UI**: Compose Multiplatform with Material3 Adaptive
- **Navigation**: Type-safe navigation with Navigation Compose
- **DI**: Koin for dependency injection
- **Database**: SQLDelight for database access
- **Async**: Kotlin Coroutines and Flow
- **Resources**: Moko Resources for multiplatform resource management

## Coding Guidelines (Critical - Always Follow)

### Naming Conventions
- **State**: `${Feature}PageState` (e.g., `TransactionPageState`)
- **Intent**: `${Feature}PageIntent` (e.g., `TransactionPageIntent`)
- **Composables**: `${Feature}PageContent` (e.g., `TransactionPageContent`)
- **ViewModels**: `${Feature}PageViewModel`
- **DI Modules**: `${Feature}Module`

### Design System Rules
- **Atomic Design**: Atoms → Molecules → Organisms hierarchy
- **Platform Parity**: Every component must work consistently across all KMP targets
- **Use Tokens**: Always use design tokens (e.g., `AppTheme.dimens.default` not `16.dp`)
- **Colors**: Access via `MaterialTheme.colorScheme` ONLY

### Code Style Requirements
- **Immutable by Default**: Use `val` unless mutation required
- **Data Classes**: Prefer for models and state
- **Sealed Interfaces**: Use for closed hierarchies (especially Intents)
- **Single Responsibility**: Keep functions focused and short
- **Stateless UI**: Pass state + intent handlers to composables
- **Modifier Last**: Place modifiers as last parameter in composables

### Error Handling & User Messaging Guidelines (CRITICAL)
**Use SnackbarService for All User Messages:**
- **SUCCESS**: Use `SnackbarService.sendSuccessMessage()` for operation confirmations
- **ERROR**: Use `SnackbarService.sendErrorMessage()` for validation errors and failures
- **WARNING**: Use `SnackbarService.sendWarningMessage()` for warnings and cautions
- **NEVER store error messages in state** - always use SnackbarService instead

**Error Message Patterns:**
```kotlin
// ✅ CORRECT - Using SnackbarService with string resources
SnackbarService.sendErrorMessage(MR.strings.account_name_required)
SnackbarService.sendSuccessMessage(MR.strings.account_create_success)
SnackbarService.sendErrorMessage(MR.strings.account_create_failure)

// ❌ WRONG - Hardcoded strings
SnackbarService.sendErrorMessage("Account name is required")

// ❌ WRONG - Storing errors in state
_state.value = _state.value.copy(error = "Account name is required")
```

**Validation Rules:**
- **Client-side validation**: Always validate required fields before submission
- **Show specific errors**: Provide clear, actionable error messages using string resources
- **String resources**: Always use `MR.strings.*` instead of hardcoded strings for user-facing messages
- **Loading states**: Use `isLoading` in state for UI feedback during operations
- **Success feedback**: Always confirm successful operations to users

### Code Quality & Linting Rules
- **KtLint**: Automatic code formatting is enforced - run `./gradlew ktlintFormat` before committing
- **DetektKT**: Static analysis catches bugs and code smells - run `./gradlew detekt` regularly
- **Max Line Length**: 120 characters (enforced by both KtLint and DetektKT)
- **No Wildcard Imports**: Use explicit imports (automatically fixed by KtLint)
- **Function Complexity**: Keep cyclomatic complexity under 15 (DetektKT rule)
- **Function Length**: Max 60 lines per function (DetektKT rule)
- **Class Size**: Max 600 lines per class (DetektKT rule)
- **Parameter Limits**: Max 6 function parameters, 7 constructor parameters (DetektKT rule)
- **Return Statements**: Max 2 return statements per function (DetektKT rule)
- **Trailing Commas**: Used consistently (KtLint formatting)
- **Build Exclusions**: Generated files, build directories, and IDE files are automatically excluded
- **EditorConfig**: Follow .editorconfig settings for consistent formatting across IDEs

### Design Token Guidelines (CRITICAL - Always Follow)
**FORBIDDEN IMPORTS**: Never import `androidx.compose.ui.unit.dp` or `androidx.compose.ui.unit.sp` outside of `AppTheme.kt`
- **Automated Enforcement**: DetektKT rule `ForbiddenImport` prevents violations
- **Build-time Verification**: CI/CD pipeline fails if forbidden imports detected

**Design Token Usage Patterns:**
```kotlin
// ❌ WRONG - Hardcoded values
import androidx.compose.ui.unit.dp
Modifier.padding(16.dp)
Modifier.size(24.dp)

// ✅ CORRECT - Design tokens
import com.lightfeather.designsystem.theme.AppTheme
Modifier.padding(AppTheme.dimens.default)
Modifier.size(AppTheme.dimens.icon.size.medium)
```

**Standard Dimension Mappings:**
- `16.dp` → `AppTheme.dimens.default`
- `8.dp` → `AppTheme.dimens.medium`
- `4.dp` → `AppTheme.dimens.small`
- `2.dp` → `AppTheme.dimens.extraSmall`
- `1.dp` → `AppTheme.dimens.hairline`
- `12.dp` → `AppTheme.dimens.compact`
- `20.dp` → `AppTheme.dimens.normal`
- `24.dp` → `AppTheme.dimens.large`
- `32.dp` → `AppTheme.dimens.extraLarge`
- `48.dp` → `AppTheme.dimens.huge`
- `64.dp` → `AppTheme.dimens.massive`

**Context-Specific Tokens:**
- **Icons**: `AppTheme.dimens.icon.size.*` (small=16dp, medium=24dp, large=32dp)
- **Components**: `AppTheme.dimens.component.button.height` (48dp)
- **Spacing**: `AppTheme.dimens.spacing.padding.medium` (16dp)
- **Elevation**: `AppTheme.dimens.elevation.level1` (1dp)
- **Touch Targets**: `AppTheme.dimens.touchTarget.min` (48dp)

**Typography Guidelines:**
- **Always Use**: `MaterialTheme.typography.bodyLarge` instead of hardcoded sp values
- **Never Import**: `androidx.compose.ui.unit.sp` (except in AppTheme.kt)
- **Font Sizes**: Defined in AppTheme.kt typography tokens only

**Exception Handling:**
- **Only AppTheme.kt** may import and define dp/sp values
- **Theme Files**: Files matching `**/theme/**/*Theme*.kt` are excluded from rules
- **Generated Files**: Build artifacts automatically excluded

### Example Template Structure
```kotlin
// State - NO error field, use SnackbarService instead
data class TransactionPageState(
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = false
)

// Intent
sealed interface TransactionPageIntent {
    data object LoadTransactions : TransactionPageIntent
    data class Delete(val id: String) : TransactionPageIntent
}

// ViewModel - Use SnackbarService with string resources
class TransactionPageViewModel : ViewModel() {
    fun onIntent(intent: TransactionPageIntent) {
        when (intent) {
            is TransactionPageIntent.Delete -> {
                // Validation
                if (someCondition) {
                    SnackbarService.sendErrorMessage(MR.strings.transaction_delete_failure)
                    return
                }

                // Success
                SnackbarService.sendSuccessMessage(MR.strings.transaction_delete_success)
            }
        }
    }
}

// Composable
@Composable
fun TransactionPageContent(
    state: TransactionPageState,
    onIntent: (TransactionPageIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    // UI implementation
}
```

## Important Implementation Notes

- **Package Structure**: Main package is `com.lightfeather.masarify`
- **Database**: Uses SQLDelight with auto-generated code
- **Localization**: Supports Arabic (RTL) and English via moko-resources
- **Navigation**: Type-safe routes defined in `navigation/Route.kt`
- **Theme**: Dark/light mode and dynamic color support
- **Platform Detection**: Use `getPlatform().asSlug()` for platform-specific logic
- **Dependency Management**: Before adding new libraries or plugins, ensure context7-mcp is running
- **Code Quality Workflow**: 
  1. Run `./gradlew ktlintFormat` to auto-format code
  2. Run `./gradlew ktlintCheck detekt` to validate quality
  3. Fix any DetektKT violations before committing
  4. All modules have KtLint and DetektKT configured globally
- **Build Exclusions**: Triple-layer exclusion system (root config + .ktlintignore + detekt.yml)
- **Source-only Scanning**: KtLint configured to only scan `src/` directories, never `build/`
- **EditorConfig**: Follow .editorconfig settings for consistent formatting across IDEs
- **Pre-commit Hooks**: Consider setting up git hooks to run quality checks automatically

### Gradle Dependency Management
- **Version Catalog**: ALL versions, plugins, and libraries are managed in `gradle/libs.versions.toml`
- **Always Use Catalog References**: Never use hardcoded versions or plugins in gradle files
- **Reference Format**: Use `libs.library.name` for dependencies and `alias(libs.plugins.plugin.name)` for plugins
- **Ask Before Hardcoding**: If hardcoded versions are absolutely necessary, ask for permission first

## App Features Overview

### 🏗️ Current Implementation Status
The app is in **early development** with basic foundation features implemented.

### 📱 Implemented Features

#### **1. Onboarding System**
- **First-time user setup** with form validation
- **User profile creation**: Name input with validation
- **Initial account setup**: 
  - Account name configuration
  - Starting balance entry (numeric input)
  - Currency selection
  - Account customization (color, logo support)
- **Form validation** with error handling for all required fields
- **Submission workflow** leading to main dashboard

#### **2. Multi-Platform Architecture**
- **Cross-platform support**: Android, iOS, Web (WASM), Desktop (JVM)
- **Adaptive navigation**: 
  - Navigation drawer for web expanded screens
  - Bottom navigation for mobile
  - Responsive layout based on screen size
- **Internationalization**: English and Arabic (RTL) support
- **Theme system**: Dark/light mode and dynamic color support

#### **3. Navigation Infrastructure**
- **Type-safe navigation** using Navigation Compose
- **Adaptive navigation suite** with platform-specific layouts
- **Route management** with dashboard and onboarding flows
- **State-aware navigation** with proper back stack handling

#### **4. Data Architecture Foundation**
- **SQLDelight database** with transaction management
- **Repository pattern** implementation for all data layers
- **Clean architecture** with domain/data/presentation separation
- **Reactive data flow** using Kotlin Flow

### 🏦 Planned Financial Features (Based on Domain Models)

#### **Transaction Management System**
- **Income tracking**: Money coming in from various sources
- **Expense management**: Categorized spending with budget tracking
- **Transfer system**: Inter-account transfers with fee calculation
- **Transaction analytics**: Min/max/average calculations
- **Transaction filtering**: Date range, category, amount filters
- **Attachment support**: Receipt and document management

#### **Account Management**
- **Multi-currency accounts**: Support for different currencies
- **Account customization**: Colors, logos, descriptions
- **Balance tracking**: Real-time balance updates
- **Account analytics**: Performance tracking across accounts

#### **Wealth Management**
- **Portfolio calculation**: Total wealth across all currencies
- **Currency conversion**: Real-time exchange rate integration
- **Wealth analytics**: Worth calculation in different currencies
- **Exchange rate tracking**: Historical and current rates

#### **Category System**
- **Income categories**: Source tracking for money coming in
- **Expense categories**: Detailed expense categorization
- **Category analytics**: Spending patterns by category
- **Custom categories**: User-defined category creation

### 🎨 Design System Features
- **Atomic design pattern**: Atoms → Molecules → Organisms
- **Material3 Adaptive**: Modern Material Design implementation
- **Cross-platform parity**: Consistent UI across all platforms
- **Design tokens**: Centralized styling system
- **Snackbar system**: User feedback and notifications

### 🔧 Technical Features
- **MVI architecture**: Model-View-Intent pattern for state management
- **Dependency injection**: Koin for modular dependency management
- **Error handling**: Comprehensive error management with DomainResult wrapper
- **Localization**: Moko Resources for multi-language support
- **Platform detection**: Smart platform-specific feature handling
- **Code quality**: KtLint for formatting and DetektKT for static analysis
- **Editor consistency**: EditorConfig for cross-IDE formatting standards

### 📊 Analytics & Reporting (Planned)
- **Transaction aggregation**: Sum totals by categories and currencies
- **Spending insights**: Pattern analysis and trends
- **Financial health**: Balance tracking and alerts
- **Export capabilities**: Data export in various formats
- **Visual dashboards**: Charts and graphs for financial visualization

### 🎯 Current App State Summary
**Masarify** is currently a **minimal viable product** focused on:
1. User onboarding and setup
2. Basic navigation infrastructure 
3. Foundational data architecture
4. Cross-platform compatibility
5. **Code quality foundation** with KtLint and DetektKT fully integrated

The core financial management features (transactions, analytics, reporting) are **architecturally planned** but **not yet implemented** in the UI layer. The app has a solid foundation with clean architecture patterns, automated code quality tools, and is ready for feature expansion.

**Key Slogan**: *"Track smart, spend wiser."* - indicating the app's focus on intelligent financial tracking and decision-making support.

## File Locations
- **Main App**: `composeApp/src/commonMain/kotlin/com/lightfeather/masarify/app/App.kt`
- **Navigation**: `composeApp/src/commonMain/kotlin/com/lightfeather/masarify/navigation/`
- **Design System**: `designsystem/src/commonMain/kotlin/com/lightfeather/designsystem/`
- **Resources**: Generated from `moko-resources` during build