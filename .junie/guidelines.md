# Project Guidelines

🧑‍💻 Masarify Coding Guidelines (For Junie AI)

These are the coding rules and style conventions for the Masarify project.
Junie should always follow these when generating code.

🏗 Project Structure

├── composeApp/           # Application Main Module
├── data/           # Repositories, local/remote sources, DTOs
├── domain/         # Entities + Use cases (business logic)
└── designsystem/         # Design system (Compose + SwiftUI parity)


Android UI → Jetpack Compose, lives in androidApp/.

iOS UI → SwiftUI, lives in iosApp/.

Business logic & data → lives in shared/.

🌀 Architecture & Patterns

MVI (Model–View–Intent) for presentation layer:

State → immutable data class.

Intent → sealed interface/class (user actions).

ViewModel → processes intents, updates state via Flow.

Domain-driven design for shared logic:

domain layer = pure, no dependencies.

data layer implements repositories & talks to persistence/APIs.

Dependency Injection:

Use Koin for shared DI.

Each feature has its own DI module.

🎨 Design System Rules

Atoms → Molecules → Organisms:

Atoms = buttons, text, icons.

Molecules = list items, cards.

Organisms = transaction list, account card, category selector.

Parity rule: Every design component must exist in both Compose & SwiftUI with the same name + API as much as possible.

Foundations (tokens):

Colors, typography, spacing, shapes live under design/tokens/.

Use tokens instead of raw values (e.g., Spacing.medium instead of 16.dp).

✍️ Kotlin Style

Follow official Kotlin style guide.

Immutable by default → use val unless mutation is required.

Prefer data classes for models.

Use sealed interfaces/classes for closed hierarchies.

Keep functions short and single-responsibility.

Use extension functions for readability.

Prefer Flows for async streams.

Example:

data class TransactionPageState(
val transactions: List<Transaction> = emptyList(),
val isLoading: Boolean = false,
val error: String? = null
)

sealed interface TransactionPageIntent {
data object LoadTransactions : TransactionPageIntent
data class Delete(val id: String) : TransactionPageIntent
}

🍏 Swift Style

Follow Swift API Design Guidelines.

Use struct for models.

Use enum for intents.

Keep SwiftUI views declarative and stateless when possible.

Example:

struct TransactionPageState {
var transactions: [Transaction] = []
var isLoading: Bool = false
var error: String? = nil
}

enum TransactionPageIntent {
case loadTransactions
case delete(id: String)
}

📦 Naming Conventions

State classes → ${Feature}PageState

Intent classes → ${Feature}PageIntent

Composable functions → ${Feature}PageContent

ViewModels → ${Feature}PageViewModel

DI modules → ${Feature}Module

🧩 Compose Rules

Stateless UI: Pass state + intent handler down.

Spacing: Use design tokens (Spacing.small, not 8.dp).

Modifiers: Place them last in parameter order.

Example:

@Composable
fun TransactionRow(
transaction: Transaction,
onClick: () -> Unit,
modifier: Modifier = Modifier
) {
Row(
modifier = modifier
.fillMaxWidth()
.clickable(onClick = onClick)
.padding(Spacing.medium)
) {
Text(transaction.title, style = MasarifyTypography.body)
Spacer(Modifier.weight(1f))
Text("${transaction.amount}", style = MasarifyTypography.bodyBold)
}
}

🗂 File Templates (Junie should use)

When creating new features:

State → data class with defaults.

Intent → sealed interface.

Composable → @Composable fun ${Feature}PageContent(...).