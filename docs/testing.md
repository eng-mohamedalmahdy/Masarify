# Testing Guide — Masarify Frontend

## Overview

Masarify uses **native Compose Multiplatform UI integration tests** instead of browser-based E2E tests. Tests live in `composeApp/src/commonTest/` and run directly on the platform, giving real signal without the fragility of screenshot-based or canvas-scraping approaches.

### Two-Tier Strategy

| Tier | Type | Location | Runner | Needs device? |
|---|---|---|---|---|
| 1 | ViewModel unit tests | `commonTest` | `runTest {}` | No |
| 2 | Composable UI tests | `commonTest` | `runComposeUiTest {}` | Yes (emulator) |

**Tier 1** tests the ViewModel logic in isolation: state mutations, navigation calls, repository call counts. No UI, no device needed.

**Tier 2** tests the rendered composable: nodes present, interactions fire correct intents, visibility with different state. Requires an Android emulator or device.

---

## Running Tests

### Tier 1 — ViewModel unit tests (no device)

```bash
# All ViewModel tests
./gradlew :composeApp:testDebugUnitTest

# Single page
./gradlew :composeApp:testDebugUnitTest --tests "tech.lightfeather.masarify.page.auth.login.LoginPageViewModelTest"
```

### Tier 2 — Composable UI tests (emulator required)

```bash
# All composable tests
./gradlew :composeApp:connectedDebugAndroidTest

# Single page
./gradlew :composeApp:connectedDebugAndroidTest --tests "tech.lightfeather.masarify.page.auth.login.LoginPageContentTest"
```

### Code quality

```bash
# Check test files only (ktlintCheck has pre-existing circular dep, use source set tasks)
./gradlew :composeApp:runKtlintCheckOverCommonTestSourceSet

# Static analysis (all modules)
./gradlew detektAll
```

---

## File Structure

```
composeApp/src/commonTest/kotlin/tech/lightfeather/masarify/
├── test/                          # Shared test infrastructure
│   ├── CapturingNavigator.kt      # Navigator fake that records calls
│   ├── FakeRepositories.kt        # All domain repository fakes
│   └── LoginViewModelBuilder.kt   # Factory for LoginPageViewModel in tests
│
└── page/
    └── auth/
        └── login/
            ├── LoginPageViewModelTest.kt   # Tier 1 — no device
            └── LoginPageContentTest.kt     # Tier 2 — needs emulator
```

---

## Build Setup

The following was added to `composeApp/build.gradle.kts`:

```kotlin
// At the top of the file
@file:OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)

// Inside kotlin { sourceSets { commonTest.dependencies { } } }
implementation(libs.kotlinx.coroutines.test)
implementation(compose.uiTest)

// Inside android { defaultConfig { } }
testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
```

---

## Patterns

### Adding tests for a new page

**Step 1 — Add `testTag` modifiers to the composable**

```kotlin
// In FooPage.kt
TextField(
    modifier = Modifier.fillMaxWidth().testTag("foo_name_field"),
    ...
)
PrimaryButton(
    modifier = Modifier.fillMaxWidth().testTag("foo_submit_button"),
    ...
)
```

**Step 2 — Add fakes to `FakeRepositories.kt`** (if needed for new repos)

`FakeRepositories.kt` already implements all domain repository interfaces. Extend it if a new page requires a new repository not yet faked.

**Step 3 — Create a ViewModel builder**

```kotlin
// test/FooViewModelBuilder.kt
internal fun buildFooViewModel(
    fooRepository: FakeFooRepository = FakeFooRepository(),
    navigator: Navigator = CapturingNavigator(),
): FooPageViewModel = FooPageViewModel(
    fooUseCase = FooUseCase(fooRepository),
    navigator = navigator,
)
```

**Step 4 — Write `FooPageViewModelTest.kt`** (Tier 1)

```kotlin
@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package tech.lightfeather.masarify.page.foo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import tech.lightfeather.masarify.test.buildFooViewModel
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class FooPageViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest fun setUp() { Dispatchers.setMain(testDispatcher) }
    @AfterTest fun tearDown() { Dispatchers.resetMain() }

    @Test
    fun updateNameIntentUpdatesStateName() = runTest {
        val vm = buildFooViewModel()
        vm.onIntent(FooPageIntent.UpdateName("Alice"))
        assertEquals("Alice", vm.state.value.name)
    }
}
```

**Step 5 — Write `FooPageContentTest.kt`** (Tier 2)

```kotlin
package tech.lightfeather.masarify.page.foo

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import tech.lightfeather.designsystem.theme.AppTheme
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class FooPageContentTest {
    @Test
    fun nameFieldIsDisplayed() = runComposeUiTest {
        setContent {
            AppTheme(useDarkTheme = false) {
                FooPageContent(state = FooPageState(), onIntent = {})
            }
        }
        onNodeWithTag("foo_name_field").assertIsDisplayed()
    }
}
```

---

## Implemented Tests

### Login page

| File | Tier | Tests |
|---|---|---|
| `LoginPageViewModelTest.kt` | 1 (no device) | 6 |
| `LoginPageContentTest.kt` | 2 (emulator) | 7 |

**ViewModel tests:**
1. `updateEmailIntentUpdatesStateEmail`
2. `updatePasswordIntentUpdatesStatePassword`
3. `submitWithBlankEmailDoesNotCallLogin`
4. `submitWithBlankPasswordDoesNotCallLogin`
5. `navigateToRegisterIntentCallsNavigateWithRegisterRoute`
6. `navigateToForgotPasswordIntentCallsNavigateWithForgotPasswordRoute`

**Composable tests** (tagged nodes: `login_email_field`, `login_password_field`, `login_submit_button`):
1. Email and password fields render in initial state
2. Submit button visible when `isLoading = false`
3. Submit button hidden when `isLoading = true`
4. Typing in email field fires `UpdateEmail` intent
5. Typing in password field fires `UpdatePassword` intent
6. Clicking submit button fires `Submit` intent
7. Email field reflects pre-filled state value

---

## Known Pitfalls

**Don't assert `SnackbarService` in ViewModel tests.**
`SnackbarService.getSnackBarMessageFlow()` uses a subscriber-count filter that doesn't play well with `UnconfinedTestDispatcher` + `MutableSharedFlow`. Snackbar assertions belong in composable UI tests (Tier 2) only.

**`runComposeUiTest` requires a device.**
Running composable tests with `testDebugUnitTest` (JVM) causes `NullPointerException`. Always use `connectedDebugAndroidTest` for Tier 2.

**Use `PagedData.create()` not the constructor.**
`PagedData(...)` has extra required parameters. Use `PagedData.create(emptyList(), page, 20, 0L)` in fakes.

**ktlint `ktlintCheck` has a pre-existing circular dependency in `:composeApp`.**
Use `./gradlew :composeApp:runKtlintCheckOverCommonTestSourceSet` to lint test files specifically. `detektAll` is unaffected.
