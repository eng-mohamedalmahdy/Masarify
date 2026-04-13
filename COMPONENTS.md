# Design System Components Documentation

This document provides a comprehensive catalog of all reusable components in the Masarify design system, organized by atomic design principles.

## Table of Contents

### [Atoms](#atoms-basic-ui-elements)
1. [Text Components](#text-components)
2. [Icon Components](#icon-components)
3. [Checkbox Components](#checkbox-components)
4. [Switch Components](#switch-components)
5. [RadioButton Components](#radiobutton-components)
6. [Divider Components](#divider-components)
7. [ImageThumbnail](#imagethumbnail)

### [Molecules](#molecules-component-combinations)
1. [TextField Components](#textfield-components)
2. [Button Components](#button-components)
3. [Chip Components](#chip-components)
4. [AppImage](#appimage)
5. [SummaryCard](#summarycard)
6. [EmptyState](#emptystate)
7. [AppDropMenu](#appdropmenu)
8. [Pagination Controls](#pagination-controls)
9. [ImageZoomDialog](#imagezoomdialog-1)
10. [Snackbar Components](#snackbar-components)
11. [Dialog Components](#dialog-components)

### [Organisms](#organisms-complex-components)
1. [TopAppBar Components](#topappbar-components)
2. [Navigation Components](#navigation-components)
3. [List Item Components](#list-item-components)
4. [AttachmentGrid](#attachmentgrid)
5. [TransactionDetailView](#transactiondetailview)
6. [AccountsHeader](#accountsheader)
7. [AccountDetailsHeader](#accountdetailsheader)
8. [Dialog Organisms](#dialog-organisms)

### [Supporting Utilities](#supporting-utilities--models)
1. [Design Token System](#design-token-system)
2. [Data Models](#data-models)
3. [Extension Functions](#extension-functions--utilities)

---

## Atoms (Basic UI Elements)

### Text Components

**File Location**: `designsystem/src/commonMain/kotlin/com/lightfeather/designsystem/component/atoms/Text.kt`

Typography-focused text composables using Material3 typography styles.

**Available Composables**:

#### Display Variants
- `DisplayLarge()` - Largest display text (typically for hero sections)
- `DisplayMedium()` - Medium display text
- `DisplaySmall()` - Small display text

#### Headline Variants
- `HeadlineLarge()` - Large headline text
- `HeadlineMedium()` - Medium headline text
- `HeadlineSmall()` - Small headline text

#### Title Variants
- `TitleLarge()` - Large title text
- `TitleMedium()` - Medium title text (commonly used for card titles)
- `TitleSmall()` - Small title text

#### Body Variants
- `BodyLarge()` - Large body text
- `BodyMedium()` - Medium body text (default paragraph text)
- `BodySmall()` - Small body text

#### Label Variants
- `LabelLarge()` - Large label text (button labels)
- `LabelMedium()` - Medium label text
- `LabelSmall()` - Small label text (captions, overlines)

**Common Parameters**:
- `text: String` - Text to display
- `modifier: Modifier = Modifier` - Modifier chain
- `color: Color = Color.Unspecified` - Text color
- `textAlign: TextAlign? = null` - Text alignment
- `overflow: TextOverflow = TextOverflow.Clip` - Overflow handling
- `maxLines: Int = Int.MAX_VALUE` - Maximum lines

**Usage Example**:
```kotlin
HeadlineMedium(
    text = "Welcome to Masarify",
    modifier = Modifier.padding(AppTheme.dimens.default),
    color = MaterialTheme.colorScheme.primary
)
```

---

### Icon Components

**File Location**: `designsystem/src/commonMain/kotlin/com/lightfeather/designsystem/component/atoms/Icon.kt`

Icon display component with size presets.

**Available Composables**:
- `Icon(imageVector: ImageVector, ...)` - Display icon from ImageVector
- `Icon(painter: Painter, ...)` - Display icon from Painter

**Parameters**:
- `imageVector: ImageVector` / `painter: Painter` - Icon source
- `contentDescription: String?` - Accessibility description
- `modifier: Modifier = Modifier` - Modifier chain
- `tint: Color = LocalContentColor.current` - Icon tint color
- `size: IconSize = IconSize.Medium` - Icon size preset

**IconSize Enum**:
- `Small` - 16.dp (e.g., trailing icons in text fields)
- `Medium` - 24.dp (default, standard icon size)
- `Large` - 32.dp (e.g., prominent actions)

**Usage Example**:
```kotlin
Icon(
    imageVector = Icons.Default.Add,
    contentDescription = "Add item",
    size = IconSize.Large,
    tint = MaterialTheme.colorScheme.primary
)
```

---

### Checkbox Components

**File Location**: `designsystem/src/commonMain/kotlin/com/lightfeather/designsystem/component/atoms/Checkbox.kt`

Checkbox selection components.

**Available Composables**:

#### Basic Checkbox
`Checkbox(checked: Boolean, onCheckedChange: ((Boolean) -> Unit)?, ...)`

**Parameters**:
- `checked: Boolean` - Current checked state
- `onCheckedChange: ((Boolean) -> Unit)?` - Change callback
- `modifier: Modifier = Modifier` - Modifier chain
- `enabled: Boolean = true` - Enable/disable state
- `colors: CheckboxColors = CheckboxDefaults.colors()` - Color scheme

#### Checkbox with Label
`CheckboxWithLabel(checked: Boolean, label: String, onCheckedChange: (Boolean) -> Unit, ...)`

**Parameters**:
- `checked: Boolean` - Current checked state
- `label: String` - Label text
- `onCheckedChange: (Boolean) -> Unit` - Change callback
- `modifier: Modifier = Modifier` - Modifier chain
- `enabled: Boolean = true` - Enable/disable state
- `colors: CheckboxColors = CheckboxDefaults.colors()` - Color scheme

**Usage Example**:
```kotlin
CheckboxWithLabel(
    checked = isSelected,
    label = "Update inverse exchange rate",
    onCheckedChange = { isSelected = it }
)
```

---

### Switch Components

**File Location**: `designsystem/src/commonMain/kotlin/com/lightfeather/designsystem/component/atoms/Switch.kt`

Toggle switch components.

**Available Composables**:

#### Basic Switch
`Switch(checked: Boolean, onCheckedChange: ((Boolean) -> Unit)?, ...)`

**Parameters**:
- `checked: Boolean` - Current checked state
- `onCheckedChange: ((Boolean) -> Unit)?` - Change callback
- `modifier: Modifier = Modifier` - Modifier chain
- `enabled: Boolean = true` - Enable/disable state
- `colors: SwitchColors = SwitchDefaults.colors()` - Color scheme

#### Switch with Label
`SwitchWithLabel(checked: Boolean, label: String, onCheckedChange: (Boolean) -> Unit, ...)`

**Parameters**:
- `checked: Boolean` - Current checked state
- `label: String` - Label text
- `onCheckedChange: (Boolean) -> Unit` - Change callback
- `modifier: Modifier = Modifier` - Modifier chain
- `enabled: Boolean = true` - Enable/disable state
- `colors: SwitchColors = SwitchDefaults.colors()` - Color scheme

**Usage Example**:
```kotlin
SwitchWithLabel(
    checked = isDarkTheme,
    label = stringResource(MR.strings.dark_theme),
    onCheckedChange = { onIntent(MorePageIntent.ToggleDarkTheme(it)) }
)
```

---

### RadioButton Components

**File Location**: `designsystem/src/commonMain/kotlin/com/lightfeather/designsystem/component/atoms/RadioButton.kt`

Radio button selection components.

**Available Composables**:

#### Basic RadioButton
`RadioButton(selected: Boolean, onClick: (() -> Unit)?, ...)`

#### RadioButton with Label
`RadioButtonWithLabel(selected: Boolean, label: String, onClick: () -> Unit, ...)`

#### Radio Group
`RadioGroup(options: List<String>, selectedOption: String, onOptionSelected: (String) -> Unit, ...)`

**RadioGroup Parameters**:
- `options: List<String>` - Available options
- `selectedOption: String` - Currently selected option
- `onOptionSelected: (String) -> Unit` - Selection callback
- `modifier: Modifier = Modifier` - Modifier chain
- `enabled: Boolean = true` - Enable/disable state

**Usage Example**:
```kotlin
RadioGroup(
    options = listOf("All", "With Attachments", "Without Attachments"),
    selectedOption = selectedFilter,
    onOptionSelected = { selectedFilter = it }
)
```

---

### Divider Components

**File Location**: `designsystem/src/commonMain/kotlin/com/lightfeather/designsystem/component/atoms/Divider.kt`

Separator line components.

**Available Composables**:

#### Horizontal Divider
`Divider(modifier: Modifier = Modifier, thickness: Dp = 1.dp, color: Color = ...)`

#### Vertical Divider
`VerticalDivider(modifier: Modifier = Modifier, thickness: Dp = 1.dp, color: Color = ...)`

**Common Parameters**:
- `modifier: Modifier = Modifier` - Modifier chain
- `thickness: Dp = 1.dp` - Line thickness
- `color: Color = MaterialTheme.colorScheme.outlineVariant` - Line color

**Usage Example**:
```kotlin
Divider(
    modifier = Modifier.padding(vertical = AppTheme.dimens.medium)
)
```

---

### ImageThumbnail

**File Location**: `designsystem/src/commonMain/kotlin/com/lightfeather/designsystem/component/atoms/ImageThumbnail.kt`

Thumbnail image component with optional delete button.

**Composable**: `ImageThumbnail(imageBytes: ByteArray, onDelete: (() -> Unit)? = null, ...)`

**Parameters**:
- `imageBytes: ByteArray` - Image data
- `onDelete: (() -> Unit)? = null` - Optional delete callback
- `contentDescription: String? = null` - Accessibility description
- `modifier: Modifier = Modifier` - Modifier chain

**Features**:
- Displays image from byte array
- Optional delete button overlay (top-right corner)
- Rounded corners
- Fixed size thumbnail

**Usage Example**:
```kotlin
ImageThumbnail(
    imageBytes = attachment.data,
    onDelete = { onDeleteAttachment(attachment) },
    contentDescription = "Receipt image"
)
```

---

## Molecules (Component Combinations)

### TextField Components

**File Location**: `designsystem/src/commonMain/kotlin/com/lightfeather/designsystem/component/molecules/TextField.kt`

Text input field components.

**Available Composables**:

#### Standard TextField
`TextField(value: String, onValueChange: (String) -> Unit, ...)`

**Parameters**:
- `value: String` - Current text value
- `onValueChange: (String) -> Unit` - Value change callback
- `modifier: Modifier = Modifier` - Modifier chain
- `enabled: Boolean = true` - Enable/disable state
- `readOnly: Boolean = false` - Read-only state
- `label: String? = null` - Label text
- `placeholder: String? = null` - Placeholder text
- `supportingText: String? = null` - Supporting/error text
- `leadingIcon: (@Composable () -> Unit)? = null` - Leading icon
- `trailingIcon: (@Composable () -> Unit)? = null` - Trailing icon
- `isError: Boolean = false` - Error state
- `visualTransformation: VisualTransformation = VisualTransformation.None` - Visual transformation
- `keyboardOptions: KeyboardOptions = KeyboardOptions.Default` - Keyboard configuration
- `keyboardActions: KeyboardActions = KeyboardActions.Default` - Keyboard actions
- `singleLine: Boolean = false` - Single line mode
- `maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE` - Maximum lines
- `colors: TextFieldColors = OutlinedTextFieldDefaults.colors()` - Color scheme

#### Password TextField
`PasswordTextField(value: String, onValueChange: (String) -> Unit, ...)`

**Features**:
- Password visibility toggle button
- Visual transformation for password masking
- All standard TextField parameters

#### Search TextField
`SearchTextField(value: String, onValueChange: (String) -> Unit, ...)`

**Additional Parameters**:
- `onSearch: () -> Unit = {}` - Search action callback
- `onClear: () -> Unit = {}` - Clear button callback

**Features**:
- Search icon leading icon
- Clear button (when text is not empty)
- Search keyboard action

**Usage Examples**:
```kotlin
// Standard TextField
TextField(
    value = accountName,
    onValueChange = { onIntent(UpdateAccountName(it)) },
    label = stringResource(MR.strings.account_name),
    isError = accountNameError != null,
    supportingText = accountNameError
)

// Password TextField
PasswordTextField(
    value = password,
    onValueChange = { password = it },
    label = "Password"
)

// Search TextField
SearchTextField(
    value = searchQuery,
    onValueChange = { searchQuery = it },
    placeholder = "Search transactions...",
    onSearch = { performSearch() },
    onClear = { searchQuery = "" }
)
```

---

### Button Components

**File Location**: `designsystem/src/commonMain/kotlin/com/lightfeather/designsystem/component/molecules/button/`

Button components with various styles.

**Available Composables**:

#### PrimaryButton
`PrimaryButton(onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true, content: @Composable RowScope.() -> Unit)`

- Filled button with primary color
- Used for primary actions (Save, Submit, Confirm)

#### SecondaryButton
`SecondaryButton(onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true, content: @Composable RowScope.() -> Unit)`

- Filled tonal button with secondary color
- Used for secondary actions

#### OutlinedButton
`OutlinedButton(onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true, content: @Composable RowScope.() -> Unit)`

- Button with border outline only
- Used for less prominent actions

#### TextButton
`TextButton(onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true, content: @Composable RowScope.() -> Unit)`

- Text-only button without background
- Used for tertiary actions (Cancel, Dismiss)

#### IconButton
`IconButton(onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true, content: @Composable () -> Unit)`
`IconButton(imageVector: ImageVector, contentDescription: String, onClick: () -> Unit, ...)`

- Circular button for icons
- Two overloads: composable content or ImageVector

#### BackButton
`BackButton(onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true)`

- Pre-configured icon button with back arrow
- Used for navigation back actions

#### FloatingActionButton Variants
- `FloatingActionButton(onClick: () -> Unit, content: @Composable () -> Unit, ...)`
- `SmallFloatingActionButton(onClick: () -> Unit, content: @Composable () -> Unit, ...)`
- `ExtendedFloatingActionButton(text: String, icon: ImageVector?, onClick: () -> Unit, ...)`

**FAB Features**:
- Standard: 56.dp size, icon content
- Small: 40.dp size, compact icon
- Extended: Icon + text label

#### SegmentedButton
`AppSegmentedButton(items: List<T>, selectedItem: T, onItemSelected: (T) -> Unit, ...)`

**Generic Parameters**: `<T>` - Item type
- Multi-select button group
- Used for filters, tabs, toggles

**Usage Examples**:
```kotlin
// Primary Button
PrimaryButton(
    onClick = { onIntent(Submit) },
    enabled = isValid && !isLoading
) {
    Text(stringResource(MR.strings.save))
}

// Icon Button
IconButton(
    imageVector = Icons.Default.Delete,
    contentDescription = "Delete",
    onClick = { showDeleteDialog = true }
)

// Back Button
BackButton(onClick = onBack)

// Extended FAB
ExtendedFloatingActionButton(
    text = stringResource(MR.strings.add_account),
    icon = Icons.Default.Add,
    onClick = { showAddDialog = true }
)

// Segmented Button
AppSegmentedButton(
    items = listOf(UiTransactionType.EXPENSE, UiTransactionType.INCOME, UiTransactionType.TRANSFER),
    selectedItem = selectedType,
    onItemSelected = { selectedType = it },
    itemLabel = { it.name }
)
```

---

### Chip Components

**File Location**: `designsystem/src/commonMain/kotlin/com/lightfeather/designsystem/component/molecules/Chip.kt`

Compact elements for selections, filters, and tags.

**Available Composables**:

#### FilterChip
`FilterChip(selected: Boolean, onClick: () -> Unit, label: String, ...)`

**Parameters**:
- `selected: Boolean` - Selection state
- `onClick: () -> Unit` - Click callback
- `label: String` - Chip label
- `modifier: Modifier = Modifier` - Modifier chain
- `enabled: Boolean = true` - Enable/disable state
- `leadingIcon: (@Composable () -> Unit)? = null` - Leading icon
- `trailingIcon: (@Composable () -> Unit)? = null` - Trailing icon
- `colors: SelectableChipColors = FilterChipDefaults.filterChipColors()` - Color scheme

#### AssistChip
`AssistChip(onClick: () -> Unit, label: String, ...)`

**Parameters**: Similar to FilterChip but without `selected` state

#### Tag
`Tag(text: String, color: Color = MaterialTheme.colorScheme.primary, onClose: (() -> Unit)? = null, ...)`

**Parameters**:
- `text: String` - Tag text
- `color: Color = MaterialTheme.colorScheme.primary` - Background color
- `textColor: Color = Color.White` - Text color
- `onClose: (() -> Unit)? = null` - Optional close callback
- `modifier: Modifier = Modifier` - Modifier chain

**Usage Examples**:
```kotlin
// Filter Chip
FilterChip(
    selected = filterType == UiTransactionType.EXPENSE,
    onClick = { filterType = UiTransactionType.EXPENSE },
    label = stringResource(MR.strings.expense)
)

// Tag
Tag(
    text = category.name,
    color = Color(category.color),
    onClose = { removeCategory(category) }
)
```

---

### AppImage

**File Location**: `designsystem/src/commonMain/kotlin/com/lightfeather/designsystem/component/molecules/AppImage.kt`

Flexible image component supporting multiple source types.

**Composable**: `AppImage(model: Any?, contentDescription: String?, ...)`

**Parameters**:
- `model: Any?` - Image source (URL, Painter, DrawableResource, ImageVector, ByteArray)
- `contentDescription: String?` - Accessibility description
- `modifier: Modifier = Modifier` - Modifier chain
- `onState: ((State) -> Unit)? = null` - State callback (loading, success, error)
- `alignment: Alignment = Alignment.Center` - Image alignment
- `contentScale: ContentScale = ContentScale.Fit` - Scaling mode
- `alpha: Float = DefaultAlpha` - Opacity
- `colorFilter: ColorFilter? = null` - Color filter
- `filterQuality: FilterQuality = DefaultFilterQuality` - Rendering quality
- `errorPlaceholder: (@Composable () -> Unit)? = null` - Error state content
- `placeholder: Painter? = null` - Loading placeholder
- `mokoPlaceholder: ImageResource? = null` - Moko resource placeholder

**Supported Model Types**:
- `String` - URL or local path
- `Painter` - Compose painter
- `DrawableResource` - Moko drawable resource
- `ImageVector` - Vector drawable
- `ByteArray` - Raw image data

**Usage Example**:
```kotlin
AppImage(
    model = account.logoUrl,
    contentDescription = account.name,
    modifier = Modifier.size(AppTheme.dimens.icon.size.large),
    contentScale = ContentScale.Crop,
    placeholder = painterResource(MR.images.placeholder_icon)
)
```

---

### SummaryCard

**File Location**: `designsystem/src/commonMain/kotlin/com/lightfeather/designsystem/component/molecules/SummaryCard.kt`

Display summary metrics in a card format.

**Composable**: `SummaryCard(value: String, label: String, ...)`

**Parameters**:
- `value: String` - Metric value (e.g., "$1,234.56")
- `label: String` - Metric label (e.g., "Total Balance")
- `modifier: Modifier = Modifier` - Modifier chain
- `valueTextStyle: TextStyle = MaterialTheme.typography.headlineMedium` - Value text style
- `labelTextStyle: TextStyle = MaterialTheme.typography.bodyMedium` - Label text style
- `cardColors: CardColors = CardDefaults.cardColors()` - Card color scheme
- `shape: Shape = CardDefaults.shape` - Card shape
- `contentPadding: PaddingValues = PaddingValues(AppTheme.dimens.default)` - Internal padding

**Usage Example**:
```kotlin
SummaryCard(
    value = "$1,234.56",
    label = stringResource(MR.strings.total_balance),
    modifier = Modifier.fillMaxWidth()
)
```

---

### EmptyState

**File Location**: `designsystem/src/commonMain/kotlin/com/lightfeather/designsystem/component/molecules/EmptyState.kt`

Display empty state with icon, title, message, and optional action.

**Composable**: `EmptyState(title: String, message: String, modifier: Modifier = Modifier, icon: ImageVector = Icons.Default.Info, action: (@Composable () -> Unit)? = null)`

**Parameters**:
- `title: String` - Empty state title
- `message: String` - Descriptive message
- `modifier: Modifier = Modifier` - Modifier chain
- `icon: ImageVector = Icons.Default.Info` - Icon to display
- `action: (@Composable () -> Unit)? = null` - Optional action button

**Usage Example**:
```kotlin
EmptyState(
    title = stringResource(MR.strings.no_transactions),
    message = stringResource(MR.strings.no_transactions_message),
    icon = Icons.Default.Receipt,
    action = {
        PrimaryButton(onClick = { showAddDialog = true }) {
            Text(stringResource(MR.strings.add_transaction))
        }
    }
)
```

---

### AppDropMenu

**File Location**: `designsystem/src/commonMain/kotlin/com/lightfeather/designsystem/component/molecules/AppDropMenu.kt`

Generic dropdown menu with search, header, and footer support.

**Composable**: `AppDropMenu<T>(label: String, displayedValue: String, items: List<T>, onSelectedItem: (T) -> Unit, ...)`

**Generic Parameters**: `<T>` - Item type

**Parameters**:
- `label: String` - Dropdown label
- `displayedValue: String` - Currently displayed value
- `items: List<T>` - Available items
- `onSelectedItem: (T) -> Unit` - Selection callback
- `modifier: Modifier = Modifier` - Modifier chain
- `dismissOnHeartClick: Boolean = true` - Auto-dismiss on item click
- `dismissOnFooterClick: Boolean = false` - Auto-dismiss on footer click
- `contentRow: @Composable (T) -> Unit` - Item renderer
- `searchFunction: ((T, String) -> Boolean)? = null` - Search filter function
- `headerContent: (@Composable () -> Unit)? = null` - Header content
- `footerContent: (@Composable () -> Unit)? = null` - Footer content (e.g., "Add New")

**Helper**: `AppDropdownContainer()` - Styled container for dropdown display

**Usage Example**:
```kotlin
AppDropMenu(
    label = stringResource(MR.strings.select_currency),
    displayedValue = selectedCurrency?.name ?: "",
    items = currencies,
    onSelectedItem = { onIntent(UpdateCurrency(it)) },
    contentRow = { currency ->
        Text("${currency.name} (${currency.symbol})")
    },
    searchFunction = { currency, query ->
        currency.name.contains(query, ignoreCase = true) ||
        currency.symbol.contains(query, ignoreCase = true)
    },
    footerContent = {
        TextButton(onClick = { showAddCurrencyDialog = true }) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(AppTheme.dimens.small))
            Text(stringResource(MR.strings.add_currency))
        }
    }
)
```

---

### Pagination Controls

**File Location**: `designsystem/src/commonMain/kotlin/com/lightfeather/designsystem/component/molecules/PaginationControls.kt`

Pagination navigation components.

**Available Composables**:

#### Standard Pagination
`PaginationControls(currentPage: Int, totalPages: Int, onPageChange: (Int) -> Unit, pageSize: PageSize, onPageSizeChange: (PageSize) -> Unit, ...)`

**Parameters**:
- `currentPage: Int` - Current page (0-indexed)
- `totalPages: Int` - Total page count
- `onPageChange: (Int) -> Unit` - Page change callback
- `pageSize: PageSize` - Current page size
- `onPageSizeChange: (PageSize) -> Unit` - Page size change callback

**Features**:
- Previous/Next buttons
- Page number display
- Page size selector dropdown

#### Advanced Pagination
`AdvancedPaginationControls(currentPage: Int, totalPages: Int, totalItems: Int, pageSize: PageSize, onPageChange: (Int) -> Unit, onPageSizeChange: (PageSize) -> Unit, ...)`

**Additional Features**:
- Direct page number selection
- Ellipsis for large page counts
- Item range display (e.g., "1-10 of 100")

#### Compact Pagination
`CompactPaginationControls(currentPage: Int, totalPages: Int, onPageChange: (Int) -> Unit, ...)`

**Features**:
- Minimal display (just page navigation)
- No page size selector

**PageSize Enum**: `TEN`, `TWENTY`, `FIFTY`, `ONE_HUNDRED`

**Usage Example**:
```kotlin
AdvancedPaginationControls(
    currentPage = currentPage,
    totalPages = totalPages,
    totalItems = totalItems,
    pageSize = pageSize,
    onPageChange = { page = it },
    onPageSizeChange = { pageSize = it }
)
```

---

### ImageZoomDialog

**File Location**: `designsystem/src/commonMain/kotlin/com/lightfeather/designsystem/component/molecules/ImageZoomDialog.kt`

Full-screen image viewer with zoom and pan gestures.

**Composable**: `ImageZoomDialog(attachments: List<UiAttachment>, initialPage: Int, onDismiss: () -> Unit, ...)`

**Parameters**:
- `attachments: List<UiAttachment>` - Images to display
- `initialPage: Int` - Starting image index
- `onDismiss: () -> Unit` - Dismiss callback
- `modifier: Modifier = Modifier` - Modifier chain

**Helper**: `ZoomableImage()` - Individual zoomable image with gestures

**Features**:
- Full-screen immersive view
- Horizontal pager for multiple images
- Pinch-to-zoom gesture
- Pan/drag when zoomed
- Double-tap to zoom
- Image counter (e.g., "1 / 3")
- Close button

**Usage Example**:
```kotlin
if (showImageViewer) {
    ImageZoomDialog(
        attachments = transactionAttachments,
        initialPage = selectedImageIndex,
        onDismiss = { showImageViewer = false }
    )
}
```

---

### Snackbar Components

**File Location**: `designsystem/src/commonMain/kotlin/com/lightfeather/designsystem/component/molecules/snackbar/`

User feedback notification system.

**Available Composables**:

#### Snackbar
`Snackbar(message: String, type: SnackbarType = SnackbarType.SUCCESS, ...)`

**Parameters**:
- `message: String` - Snackbar message
- `type: SnackbarType` - Snackbar type (SUCCESS, WARNING, ERROR)
- `modifier: Modifier = Modifier` - Modifier chain
- `leadingContent: (@Composable () -> Unit)? = null` - Leading icon/content
- `actionContent: (@Composable () -> Unit)? = null` - Action button content

**SnackbarHost**
`SnackbarHost(hostState: SnackbarHostState, ...)`

#### SnackbarService Object
Centralized service for sending messages.

**Functions**:
- `sendSuccessMessage(message: String)` - Success message
- `sendSuccessMessage(stringResource: StringResource)` - Success with resource
- `sendWarningMessage(message: String)` - Warning message
- `sendWarningMessage(stringResource: StringResource)` - Warning with resource
- `sendErrorMessage(message: String)` - Error message
- `sendErrorMessage(stringResource: StringResource)` - Error with resource
- `sendErrorMessage(textMessage: TextMessage)` - Error with TextMessage
- `sendMessage(message: SnackbarMessage)` - Custom message

**Extension Function**:
`CoroutineScope.handleSnackbarMessages(snackbarHostState: SnackbarHostState)`

**Data Classes**:
- `SnackbarMessage` - Message with text, type, duration
- `TextMessage` - Either string or StringResource
- `SnackbarType` - Enum (SUCCESS, WARNING, ERROR)

**Usage Examples**:
```kotlin
// In ViewModel
SnackbarService.sendSuccessMessage(MR.strings.account_created_success)
SnackbarService.sendErrorMessage(MR.strings.account_name_required)

// In Composable
val snackbarHostState = remember { SnackbarHostState() }

LaunchedEffect(Unit) {
    handleSnackbarMessages(snackbarHostState)
}

Scaffold(
    snackbarHost = { SnackbarHost(snackbarHostState) }
) {
    // Content
}
```

---

### Dialog Components

**File Location**: `designsystem/src/commonMain/kotlin/com/lightfeather/designsystem/component/molecules/dialog/`

#### AppAlertDialog
`AppAlertDialog(title: String, message: String, onDismissRequest: () -> Unit, onConfirm: () -> Unit, ...)`

Simple confirmation dialog.

**Parameters**:
- `title: String` - Dialog title
- `message: String` - Dialog message
- `onDismissRequest: () -> Unit` - Dismiss callback
- `onConfirm: () -> Unit` - Confirm callback
- `modifier: Modifier = Modifier` - Modifier chain

#### AppInputAlertDialog
`AppInputAlertDialog(title: String, message: String, inputValue: String, onValueChange: (String) -> Unit, onDismissRequest: () -> Unit, onConfirm: (String) -> Unit, ...)`

Alert dialog with text input.

**Additional Parameters**:
- `inputValue: String` - Current input value
- `onValueChange: (String) -> Unit` - Input change callback
- `inputLabel: String = ""` - Input field label
- `keyboardType: KeyboardType = KeyboardType.Text` - Keyboard type

**Usage Examples**:
```kotlin
// Simple Alert
AppAlertDialog(
    title = stringResource(MR.strings.delete_account),
    message = stringResource(MR.strings.delete_account_message, accountName),
    onDismissRequest = { showDeleteDialog = false },
    onConfirm = {
        onIntent(DeleteAccount(account))
        showDeleteDialog = false
    }
)

// Input Alert
AppInputAlertDialog(
    title = stringResource(MR.strings.save_filter),
    message = stringResource(MR.strings.enter_filter_name),
    inputValue = filterName,
    onValueChange = { filterName = it },
    onDismissRequest = { showSaveDialog = false },
    onConfirm = { name ->
        onIntent(SaveFilter(name, currentFilter))
        showSaveDialog = false
    }
)
```

---

## Organisms (Complex Components)

### TopAppBar Components

**File Location**: `designsystem/src/commonMain/kotlin/com/lightfeather/designsystem/component/organisms/topbar/`

Top app bar components for page headers.

**Available Composables**:

#### Standard TopAppBar
`TopAppBar(title: String, modifier: Modifier = Modifier, supportingContent: (@Composable () -> Unit)? = null, navigationIcon: (@Composable () -> Unit)? = null, actions: (@Composable RowScope.() -> Unit)? = null)`

**Parameters**:
- `title: String` - App bar title
- `modifier: Modifier = Modifier` - Modifier chain
- `supportingContent: (@Composable () -> Unit)? = null` - Content below title
- `navigationIcon: (@Composable () -> Unit)? = null` - Leading icon (e.g., back button)
- `actions: (@Composable RowScope.() -> Unit)? = null` - Trailing actions

#### TopAppBar with Back and Full Title
`TopAppBarWithBackAndFullTitle(title: String, onBackClick: () -> Unit, modifier: Modifier = Modifier, actions: (@Composable RowScope.() -> Unit)? = null)`

**Features**:
- Pre-configured back button
- Full-width title
- Optional trailing actions

**Usage Examples**:
```kotlin
// Standard TopAppBar
TopAppBar(
    title = stringResource(MR.strings.bank_accounts),
    navigationIcon = {
        IconButton(onClick = { /* menu */ }) {
            Icon(Icons.Default.Menu, contentDescription = "Menu")
        }
    },
    actions = {
        IconButton(onClick = { showFilterDialog = true }) {
            Icon(Icons.Default.FilterList, contentDescription = "Filter")
        }
    }
)

// TopAppBar with Back
TopAppBarWithBackAndFullTitle(
    title = stringResource(MR.strings.add_category),
    onBackClick = onBack,
    actions = {
        IconButton(onClick = { onIntent(Save) }) {
            Icon(Icons.Default.Check, contentDescription = "Save")
        }
    }
)
```

---

### Navigation Components

**File Location**: `designsystem/src/commonMain/kotlin/com/lightfeather/designsystem/component/organisms/`

Adaptive navigation component supporting multiple layouts.

#### AppNavigationSuite
`AppNavigationSuite(navigationSuiteType: NavigationSuiteType, modifier: Modifier = Modifier, navigationSuiteColors: NavigationSuiteColors = ..., navigationItemVerticalArrangement: Arrangement.Vertical = ..., primaryActionContent: (@Composable () -> Unit)? = null, content: AppNavigationSuiteScope.() -> Unit, builder: (AppNavigationSuiteScope.() -> Unit)? = null)`

**Parameters**:
- `navigationSuiteType: NavigationSuiteType` - Layout type (NavigationBar, NavigationRail, NavigationDrawer, etc.)
- `modifier: Modifier = Modifier` - Modifier chain
- `navigationSuiteColors: NavigationSuiteColors` - Color scheme
- `navigationItemVerticalArrangement: Arrangement.Vertical` - Item arrangement
- `primaryActionContent: (@Composable () -> Unit)? = null` - FAB or primary action
- `content: AppNavigationSuiteScope.() -> Unit` - DSL for items
- `builder: (AppNavigationSuiteScope.() -> Unit)? = null` - Customization DSL

**DSL Functions**:
- `item(selected: Boolean, onClick: () -> Unit, icon: ImageVector, label: String, badge: String? = null, colors: AppNavigationItemColors? = null)`

**Data Classes**:
- `AppNavItem` - Navigation item configuration
- `AppNavigationItemColors` - Color configuration for nav items

**Features**:
- Adaptive layout (bottom bar, rail, drawer)
- Icon + label navigation items
- Badge support
- Custom colors per item
- Primary action slot (FAB)

**Usage Example**:
```kotlin
AppNavigationSuite(
    navigationSuiteType = adaptiveNavigationSuiteType,
    primaryActionContent = {
        ExtendedFloatingActionButton(
            text = stringResource(MR.strings.add_transaction),
            icon = Icons.Default.Add,
            onClick = { showAddDialog = true }
        )
    }
) {
    item(
        selected = currentRoute == Route.BankAccounts,
        onClick = { navigate(Route.BankAccounts) },
        icon = Icons.Default.AccountBalance,
        label = stringResource(MR.strings.accounts)
    )
    item(
        selected = currentRoute == Route.Transactions,
        onClick = { navigate(Route.Transactions) },
        icon = Icons.Default.Receipt,
        label = stringResource(MR.strings.transactions)
    )
    item(
        selected = currentRoute == Route.Categories,
        onClick = { navigate(Route.Categories) },
        icon = Icons.Default.Category,
        label = stringResource(MR.strings.categories)
    )
    item(
        selected = currentRoute == Route.More,
        onClick = { navigate(Route.More) },
        icon = Icons.Default.MoreHoriz,
        label = stringResource(MR.strings.more)
    )
}
```

---

### List Item Components

**File Location**: `designsystem/src/commonMain/kotlin/com/lightfeather/designsystem/component/organisms/listitem/`

Specialized list item components for different data types.

#### BankAccountItem
`BankAccountItem(bankAccount: UiBankAccount, modifier: Modifier = Modifier, shape: Shape = CardDefaults.shape, onClick: () -> Unit, onTransfer: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit, onCreateTransaction: () -> Unit)`

**Parameters**:
- `bankAccount: UiBankAccount` - Account data
- `modifier: Modifier = Modifier` - Modifier chain
- `shape: Shape = CardDefaults.shape` - Card shape
- `onClick: () -> Unit` - Click callback
- `onTransfer: () -> Unit` - Transfer button callback
- `onEdit: () -> Unit` - Edit button callback
- `onDelete: () -> Unit` - Delete button callback
- `onCreateTransaction: () -> Unit` - Add transaction callback

**Features**:
- Card-based layout
- Account name and description
- Balance display with currency
- Color indicator
- Action buttons (Add Transaction, Transfer, Edit, Delete)
- Selection animation

**Helper Composables**:
- `ActionButton()` - Icon button for actions
- `BankAccountActions()` - Row of action buttons

#### TransactionItem
`TransactionItem(transaction: UiTransaction, modifier: Modifier = Modifier, onClick: () -> Unit)`

**Parameters**:
- `transaction: UiTransaction` - Transaction data
- `modifier: Modifier = Modifier` - Modifier chain
- `onClick: () -> Unit` - Click callback

**Features**:
- Category color indicator (leading)
- Transaction name and category
- Date display
- Amount with sign and currency
- Type indicator (income/expense/transfer)

#### CategoryListItem
Category display list item (details in file but not fully documented in task output)

#### MoreListItem
Settings/more page list item (details in file but not fully documented in task output)

**Usage Examples**:
```kotlin
// Bank Account Item
BankAccountItem(
    bankAccount = account,
    onClick = { onIntent(SelectAccount(account)) },
    onTransfer = { onIntent(TransferFromAccount(account)) },
    onEdit = { showEditDialog = true },
    onDelete = { showDeleteDialog = true },
    onCreateTransaction = { onIntent(CreateTransactionInAccount(account)) }
)

// Transaction Item
TransactionItem(
    transaction = transaction,
    onClick = { onIntent(SelectTransaction(transaction)) }
)
```

---

### AttachmentGrid

**File Location**: `designsystem/src/commonMain/kotlin/com/lightfeather/designsystem/component/organisms/AttachmentGrid.kt`

Grid display for image attachments.

**Composable**: `AttachmentGrid(attachments: List<UiAttachment>, onDelete: ((UiAttachment) -> Unit)? = null, modifier: Modifier = Modifier)`

**Parameters**:
- `attachments: List<UiAttachment>` - Images to display
- `onDelete: ((UiAttachment) -> Unit)? = null` - Optional delete callback
- `modifier: Modifier = Modifier` - Modifier chain

**Features**:
- 3-column grid layout
- Thumbnail images with delete buttons
- Click to view full-screen
- Opens ImageZoomDialog on tap

**Usage Example**:
```kotlin
AttachmentGrid(
    attachments = transactionAttachments,
    onDelete = { attachment ->
        onIntent(DeleteAttachment(attachment))
    }
)
```

---

### TransactionDetailView

**File Location**: `designsystem/src/commonMain/kotlin/com/lightfeather/designsystem/component/organisms/TransactionDetailView.kt`

Comprehensive transaction detail display.

**Composable**: `TransactionDetailView(transaction: UiTransactionDetails, attachments: List<UiAttachment>, onEdit: () -> Unit, onDelete: () -> Unit, onDuplicate: () -> Unit, modifier: Modifier = Modifier)`

**Parameters**:
- `transaction: UiTransactionDetails` - Transaction data
- `attachments: List<UiAttachment>` - Transaction attachments
- `onEdit: () -> Unit` - Edit button callback
- `onDelete: () -> Unit` - Delete button callback
- `onDuplicate: () -> Unit` - Duplicate button callback
- `modifier: Modifier = Modifier` - Modifier chain

**Features**:
- Type badge (Expense/Income/Transfer) with color
- Transaction name
- Amount with currency and type indicator
- Account information (with special transfer view: from → to)
- Multiple categories display
- Date and time
- Description (if present)
- Attachment grid (if present)
- Action buttons row (Edit, Duplicate, Delete)
- Type-specific fields

**Usage Example**:
```kotlin
TransactionDetailView(
    transaction = selectedTransaction,
    attachments = transactionAttachments[selectedTransaction.id] ?: emptyList(),
    onEdit = { onIntent(ShowEditDialog(selectedTransaction)) },
    onDelete = { onIntent(DeleteTransaction(selectedTransaction)) },
    onDuplicate = { onIntent(DuplicateTransaction(selectedTransaction)) }
)
```

---

### AccountsHeader

**File Location**: `designsystem/src/commonMain/kotlin/com/lightfeather/designsystem/component/organisms/AccountsHeader.kt`

Wealth summary header for accounts page (details in file but not fully documented in task output).

---

### AccountDetailsHeader

**File Location**: `designsystem/src/commonMain/kotlin/com/lightfeather/designsystem/component/organisms/AccountDetailsHeader.kt`

Account detail header component (details in file but not fully documented in task output).

---

### Dialog Organisms

**File Location**: `designsystem/src/commonMain/kotlin/com/lightfeather/designsystem/component/organisms/dialog/`

Complex dialog components documented in the [Dialogs section](#dialogs) of PAGES_AND_DIALOGS.md:

- **AdvancedFilterDialog** - Transaction filtering with multiple criteria
- **AddEditTransactionDialog** - Transaction creation/editing with type tabs
- **ColorPickerDialog** - HSV color picker with hex input
- **AppInputAlertDialog** - Alert with text input

---

## Supporting Utilities & Models

### Design Token System

**File Location**: `designsystem/src/commonMain/kotlin/com/lightfeather/designsystem/theme/AppTheme.kt`

Centralized design token system for consistent styling.

**Usage**: Access via `AppTheme` object

#### Dimensions (`AppTheme.dimens`)

**Standard Spacing**:
- `hairline` - 1.dp (divider lines)
- `extraSmall` - 2.dp
- `small` - 4.dp (tight spacing)
- `medium` - 8.dp (compact spacing)
- `compact` - 12.dp
- `default` - 16.dp (standard spacing)
- `normal` - 20.dp
- `large` - 24.dp (spacious padding)
- `extraLarge` - 32.dp
- `huge` - 48.dp (section spacing)
- `massive` - 64.dp (page margins)

**Context-Specific Tokens**:
- `icon.size.small` - 16.dp (trailing icons)
- `icon.size.medium` - 24.dp (standard icons)
- `icon.size.large` - 32.dp (prominent icons)
- `component.button.height` - 48.dp (button min height)
- `spacing.padding.medium` - 16.dp (content padding)
- `elevation.level1` - 1.dp (card elevation)
- `touchTarget.min` - 48.dp (minimum touch target)

#### Shapes (`AppTheme.shapes`)
- Shape definitions for cards, buttons, dialogs

#### Colors (`AppTheme.colors`)
- Custom colors beyond Material3 scheme
- `success` - Success state color
- `secondary` - Secondary accent color

#### Typography (`AppTheme.typography`)
- Typography styles (use `MaterialTheme.typography` instead)

**Usage Example**:
```kotlin
// ✅ CORRECT - Using design tokens
Box(
    modifier = Modifier
        .padding(AppTheme.dimens.default)
        .size(AppTheme.dimens.icon.size.large)
)

// ❌ WRONG - Hardcoded values
Box(
    modifier = Modifier
        .padding(16.dp)
        .size(32.dp)
)
```

**CRITICAL RULE**: Never import `androidx.compose.ui.unit.dp` or `androidx.compose.ui.unit.sp` outside of `AppTheme.kt`. Always use design tokens.

---

### Data Models

**File Location**: `designsystem/src/commonMain/kotlin/com/lightfeather/designsystem/model/`

UI data models used across components.

**Available Models**:

#### UiBankAccount
- Bank account display data
- Properties: id, name, description, balance, currency, color, logoUrl, isDefault

#### UiTransaction
- Transaction list item data
- Properties: id, name, amount, category, date, type, account

#### UiTransactionDetails
- Detailed transaction view data
- Properties: All UiTransaction properties + description, attachments, categories (list), fromAccount, toAccount, transferFee

#### UiTransactionType (Enum)
- `INCOME` - Money coming in
- `EXPENSE` - Money going out
- `TRANSFER` - Money between accounts

#### UiAttachment
- File attachment data
- Properties: id, data (ByteArray), fileName, mimeType, size

#### UiCategory
- Category data with localization
- Properties: id, name, description, color, iconUrl, type, localizedName

#### UiCurrency
- Currency data
- Properties: id, name, symbol, isDefault

#### UiTransactionFilter
- Filter configuration for transactions
- Properties: searchQuery, types, accountIds, categoryIds, currencyIds, minAmount, maxAmount, hasAttachments, filterLogic (AND/OR)

#### PageSize (Enum)
- `TEN`, `TWENTY`, `FIFTY`, `ONE_HUNDRED` - Pagination page sizes

#### SavedFilter
- Saved filter preset
- Properties: name, filter (UiTransactionFilter)

#### UiBankName
- Bank name/branding display data (for bank selection dropdowns)
- Properties: id, name, resourceKey, logoUrl, isDefault
- `getLocalizedName(): String` - Returns localized bank name (Composable extension)
- Includes built-in banks from `DefaultBankNames` enum (26 banks + Cash + Custom)

---

### Extension Functions & Utilities

**File Locations**: `designsystem/src/commonMain/kotlin/com/lightfeather/designsystem/util/`

#### ColorExt.kt
- `colorToHex(color: Color): String` - Convert Color to hex string (#RRGGBB)
- `toColorInt(color: Color): Int` - Convert Color to ARGB integer

**Usage**:
```kotlin
val hexColor = colorToHex(Color.Red) // "#FF0000"
```

#### DateTimeExt.kt
- Date and time formatting utilities
- Platform-specific date formatting

#### ResourcesExt.kt
- Resource handling utilities
- String resource access helpers

#### CategoryLocalizationMapper.kt
- Category name localization
- Maps default categories to localized names

**Usage**:
```kotlin
val localizedName = category.getLocalizedName(currentLanguage)
```

---

## Component Organization Summary

### Statistics
- **Total Components**: 50+ reusable components
- **Atoms**: 10 basic building blocks
- **Molecules**: 20+ component combinations
- **Organisms**: 15+ complex feature components
- **Utilities**: 5+ supporting services and extensions

### Component Hierarchy

```
Design System
├── Atoms (Basic Elements)
│   ├── Text (15 variants)
│   ├── Icon (2 overloads)
│   ├── Checkbox (2 variants)
│   ├── Switch (2 variants)
│   ├── RadioButton (3 variants)
│   ├── Divider (2 variants)
│   └── ImageThumbnail
│
├── Molecules (Combinations)
│   ├── TextField (3 variants)
│   ├── Button (9 variants)
│   ├── Chip (3 variants)
│   ├── AppImage
│   ├── SummaryCard
│   ├── EmptyState
│   ├── AppDropMenu
│   ├── Pagination (3 variants)
│   ├── ImageZoomDialog
│   ├── Snackbar (+ SnackbarService)
│   └── Dialog (2 variants)
│
├── Organisms (Complex Components)
│   ├── TopAppBar (2 variants)
│   ├── AppNavigationSuite
│   ├── BankAccountItem
│   ├── TransactionItem
│   ├── CategoryListItem
│   ├── MoreListItem
│   ├── AttachmentGrid
│   ├── TransactionDetailView
│   ├── AccountsHeader
│   ├── AccountDetailsHeader
│   └── Complex Dialogs (4 variants)
│
└── Utilities & Theme
    ├── AppTheme (Design Tokens)
    ├── Data Models (10+ models)
    └── Extensions (4 utility files)
```

---

## Design System Principles

### 1. Atomic Design Pattern
Components are organized in a hierarchy from simple to complex:
- **Atoms**: Cannot be broken down further (Text, Icon, Button)
- **Molecules**: Combinations of atoms (TextField with icon, Card with content)
- **Organisms**: Complex features (Navigation suite, Transaction detail view)

### 2. Design Token Usage
**CRITICAL**: Always use design tokens instead of hardcoded values.

```kotlin
// ✅ CORRECT
Modifier.padding(AppTheme.dimens.default)
Modifier.size(AppTheme.dimens.icon.size.medium)

// ❌ WRONG
Modifier.padding(16.dp)
Modifier.size(24.dp)
```

### 3. Material3 Integration
All components follow Material Design 3 guidelines:
- Color scheme from `MaterialTheme.colorScheme`
- Typography from `MaterialTheme.typography`
- Shape system from `MaterialTheme.shapes`

### 4. Platform Parity
Every component works consistently across:
- Android
- iOS
- Web (WASM)
- Desktop (JVM)

### 5. Accessibility
- Content descriptions for images and icons
- Semantic structure for screen readers
- Proper focus handling
- Minimum touch target sizes (48.dp)

### 6. Stateless UI
Components are stateless and receive:
- State via parameters
- Callbacks via lambdas
- No internal state management

### 7. Composition Over Inheritance
- Use `@Composable` functions instead of classes
- Prefer composition with slots (content, leadingIcon, etc.)
- DSL builders for complex configurations

---

## Usage Patterns

### Common Patterns

#### Form Validation
```kotlin
TextField(
    value = accountName,
    onValueChange = { onIntent(UpdateAccountName(it)) },
    label = stringResource(MR.strings.account_name),
    isError = accountNameError != null,
    supportingText = accountNameError
)
```

#### Loading States
```kotlin
PrimaryButton(
    onClick = { onIntent(Submit) },
    enabled = !isLoading
) {
    if (isLoading) {
        CircularProgressIndicator(
            modifier = Modifier.size(AppTheme.dimens.icon.size.small)
        )
    } else {
        Text(stringResource(MR.strings.save))
    }
}
```

#### Conditional Content
```kotlin
if (items.isEmpty()) {
    EmptyState(
        title = stringResource(MR.strings.no_items),
        message = stringResource(MR.strings.no_items_message)
    )
} else {
    LazyColumn {
        items(items) { item ->
            ItemRow(item = item)
        }
    }
}
```

#### Dialog Management
```kotlin
if (showDialog) {
    AppAlertDialog(
        title = stringResource(MR.strings.confirm_delete),
        message = stringResource(MR.strings.delete_message),
        onDismissRequest = { showDialog = false },
        onConfirm = {
            onIntent(DeleteItem(item))
            showDialog = false
        }
    )
}
```

---

## Best Practices

### 1. Always Use Design Tokens
```kotlin
// ✅ CORRECT
Modifier.padding(AppTheme.dimens.default)

// ❌ WRONG
Modifier.padding(16.dp)
```

### 2. Use String Resources
```kotlin
// ✅ CORRECT
Text(stringResource(MR.strings.account_name))
SnackbarService.sendSuccessMessage(MR.strings.account_created)

// ❌ WRONG
Text("Account Name")
SnackbarService.sendSuccessMessage("Account created")
```

### 3. Provide Content Descriptions
```kotlin
// ✅ CORRECT
Icon(
    imageVector = Icons.Default.Add,
    contentDescription = "Add item"
)

// ❌ WRONG (accessibility issue)
Icon(
    imageVector = Icons.Default.Add,
    contentDescription = null
)
```

### 4. Handle Loading States
```kotlin
// ✅ CORRECT
PrimaryButton(
    onClick = { onIntent(Save) },
    enabled = !isLoading && isValid
) {
    if (isLoading) {
        CircularProgressIndicator()
    } else {
        Text(stringResource(MR.strings.save))
    }
}
```

### 5. Use Proper Error Handling
```kotlin
// ✅ CORRECT - Use SnackbarService
SnackbarService.sendErrorMessage(MR.strings.account_name_required)

// ❌ WRONG - Don't store error in state
_state.value = _state.value.copy(error = "Account name required")
```

---

## Migration Guide

### Adding New Components

1. **Determine Component Level**:
   - Atom: Basic, indivisible element
   - Molecule: Combination of atoms
   - Organism: Complex feature component

2. **Create File in Appropriate Directory**:
   - Atoms: `designsystem/component/atoms/`
   - Molecules: `designsystem/component/molecules/`
   - Organisms: `designsystem/component/organisms/`

3. **Follow Naming Conventions**:
   - Composables: PascalCase (e.g., `PrimaryButton`)
   - Parameters: camelCase (e.g., `onClick`)
   - Data classes: PascalCase with `Ui` prefix (e.g., `UiBankAccount`)

4. **Use Design Tokens**:
   - Never hardcode dp/sp values
   - Use `AppTheme.dimens.*` for spacing
   - Use `MaterialTheme.colorScheme.*` for colors

5. **Add Preview Functions**:
   ```kotlin
   @Preview
   @Composable
   private fun MyComponentPreview() {
       AppTheme {
           MyComponent()
       }
   }
   ```

6. **Document Parameters**:
   - Add KDoc comments for public components
   - Document complex behavior
   - Provide usage examples

---

## Component Quick Reference

### Most Used Components

| Component | Purpose | Common Usage |
|-----------|---------|--------------|
| `PrimaryButton` | Primary actions | Save, Submit, Confirm |
| `TextField` | Text input | Forms, search |
| `AppDropMenu` | Selection | Currency, Category selection |
| `AppAlertDialog` | Confirmations | Delete, Logout |
| `EmptyState` | No data display | Empty lists |
| `SnackbarService` | User feedback | Success/Error messages |
| `TopAppBar` | Page header | Navigation, title |
| `AppNavigationSuite` | App navigation | Bottom bar, drawer, rail |
| `FilterChip` | Filtering | Multi-select filters |
| `FloatingActionButton` | Primary action | Add item |

### Design Token Quick Reference

| Use Case | Token | Value |
|----------|-------|-------|
| Standard padding | `AppTheme.dimens.default` | 16.dp |
| Compact padding | `AppTheme.dimens.medium` | 8.dp |
| Section spacing | `AppTheme.dimens.large` | 24.dp |
| Standard icon | `AppTheme.dimens.icon.size.medium` | 24.dp |
| Small icon | `AppTheme.dimens.icon.size.small` | 16.dp |
| Button height | `AppTheme.dimens.component.button.height` | 48.dp |
| Touch target | `AppTheme.dimens.touchTarget.min` | 48.dp |

---

*Last Updated: 2026-03-23*
