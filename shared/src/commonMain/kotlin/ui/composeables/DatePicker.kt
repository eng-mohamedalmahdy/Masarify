package ui.composeables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

@Composable
fun DatePicker(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,

    daysColor: Color = MaterialTheme.colorScheme.onPrimary,
    selectedDayTextColor: Color = MaterialTheme.colorScheme.primary,
) {

    var currentDate by remember { mutableStateOf(Clock.System.now()) }
    var selectedYear by remember { mutableIntStateOf(selectedDate.year) }
    var selectedMonth by remember { mutableIntStateOf(selectedDate.monthNumber) }
    var selectedDay by remember { mutableIntStateOf(selectedDate.dayOfMonth) }


    @ReadOnlyComposable
    @Composable
    fun selectedDayColor(isSelected: Boolean) =
        if (isSelected) MaterialTheme.colorScheme.onPrimary else Color.Transparent

    Column(modifier = Modifier.padding(16.dp)) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(16.dp))
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Month Chooser
                    YearPicker(
                        value = selectedYear,
                        onValueChange = { selectedYear = it.toInt() },
                        modifier = Modifier.weight(.5f)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                currentDate =
                                    currentDate.minus(DateTimePeriod(months = 1), TimeZone.currentSystemDefault())

                                selectedMonth = currentDate.toLocalDateTime(TimeZone.currentSystemDefault()).monthNumber
                            }
                        ) {
                            Icon(
                                Icons.Default.KeyboardArrowLeft,
                                contentDescription = "Previous Month",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        Text(
                            text = Month(selectedMonth).name.lowercase().replaceFirstChar { it.uppercase() },
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )

                        IconButton(
                            onClick = {
                                currentDate =
                                    currentDate.plus(DateTimePeriod(months = 1), TimeZone.currentSystemDefault())

                                selectedMonth = currentDate.toLocalDateTime(TimeZone.currentSystemDefault()).monthNumber
                            }
                        ) {
                            Icon(
                                Icons.Default.KeyboardArrowRight,
                                contentDescription = "Next Month",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                }
                Divider()

                // Days Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    for (day in daysOfWeek) {
                        Text(
                            text = day,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(8.dp),
                            color = daysColor
                        )
                    }
                }

                val monthNumberFormatted = if (selectedMonth > 9) "$selectedMonth" else "0$selectedMonth"
                val currentMonth = Instant.parse("${selectedYear}-$monthNumberFormatted-01T00:00:00.000Z")
                val nextMonth = currentMonth.plus(DateTimePeriod(months = 1), TimeZone.currentSystemDefault())
                val daysInMonth = currentMonth.daysUntil(nextMonth, TimeZone.currentSystemDefault())
                val firstDayOfWeek =
                    currentMonth.toLocalDateTime(TimeZone.currentSystemDefault()).dayOfWeek.isoDayNumber

                val monthDays = (1..daysInMonth).toList()
                val emptyCellsCount = (firstDayOfWeek - 1) % 7

                val displayableDays = List(emptyCellsCount) { "" } + monthDays.map(Int::toString)
                LazyVerticalGrid(columns = GridCells.Fixed(7)) {
                    displayableDays.forEach { day: String ->
                        val isSelected =
                            day.toIntOrNull() == selectedDay &&
                                    selectedMonth == selectedDate.monthNumber &&
                                    selectedYear == selectedDate.year


                        item {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .size(52.dp)
                                    .clickable {

                                        selectedDay = day.toIntOrNull() ?: selectedDay
                                        onDateSelected(
                                            LocalDate(
                                                year = selectedYear,
                                                month = Month(selectedMonth),
                                                dayOfMonth = selectedDay
                                            )
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day,
                                    textAlign = TextAlign.Center,
                                    color = if (isSelected) selectedDayTextColor else MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .background(selectedDayColor(isSelected), shape = CircleShape)
                                        .padding(8.dp),
                                    fontWeight = FontWeight.SemiBold

                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun YearPicker(
    value: Int,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val years = (1400..50000).toList().map { it.toString() }

    val currentIndex = years.indexOf(value.toString())

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                val newIndex = (currentIndex - 1).coerceIn(0, years.size - 1)
                onValueChange(years[newIndex])
            }
        ) {
            Icon(
                Icons.Default.KeyboardArrowLeft,
                contentDescription = "Increase Month",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = modifier.weight(.15f)
            )
        }

        Text(
            text = years[currentIndex],
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.onPrimary
        )

        IconButton(
            onClick = {
                val newIndex = (currentIndex + 1).coerceIn(0, years.size - 1)
                onValueChange(years[newIndex])
            }
        ) {
            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = "Decrease Month",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = modifier.weight(.15f)
            )
        }
    }
}