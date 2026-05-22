package com.example.calendarapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calendarapp.ui.components.CalendarGrid
import com.example.calendarapp.ui.components.HolidayDialog
import com.example.calendarapp.viewmodel.CalendarViewModel
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    vm: CalendarViewModel = viewModel(),
    onSettingsClick: () -> Unit = {}
) {
    val ym by vm.currentYearMonth.collectAsState()
    val selDate by vm.selectedDate.collectAsState()
    val selHolidays by vm.selectedHolidays.collectAsState()
    val showDialog by vm.showDialog.collectAsState()
    val days by vm.daysInMonth.collectAsState()
    val today by vm.today.collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        // Заголовок с навигацией и кнопкой настроек
        Card(Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
            Row(
                Modifier.fillMaxWidth().padding(8.dp),
                Arrangement.SpaceBetween,
                Alignment.CenterVertically
            ) {
                IconButton({ vm.previousMonth() }) {
                    Icon(Icons.Default.ChevronLeft, "Назад")
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        ym.month.getDisplayName(
                            TextStyle.FULL_STANDALONE,
                            Locale("ru")
                        ).replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        ym.year.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            Icons.Default.Settings,
                            "Настройки",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton({ vm.nextMonth() }) {
                        Icon(Icons.Default.ChevronRight, "Вперед")
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Сетка календаря
        CalendarGrid(
            days = days,
            selectedDate = selDate,
            today = today,
            getHoliday = { vm.getMajorHoliday(it) },
            onDateClick = { vm.onDateClick(it) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        // Список праздников месяца
        Text(
            "Праздники в этом месяце:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            val monthHolidays = days
                .filterNotNull()
                .flatMap { date ->
                    val h = vm.getMajorHoliday(date)
                    if (h != null) listOf(h to date.dayOfMonth) else emptyList()
                }
                .distinctBy { it.first.name }

            if (monthHolidays.isNotEmpty()) {
                monthHolidays.forEach { (h, day) ->
                    Card(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                h.emoji,
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    h.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    "$day ${
                                        ym.month.getDisplayName(
                                            TextStyle.FULL_STANDALONE,
                                            Locale("ru")
                                        )
                                    }",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            } else {
                Text(
                    "Нет важных праздников",
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    // Диалог с праздниками
    val dialogDate by vm.dialogDate.collectAsState()

    if (showDialog) {
        HolidayDialog(
            holidays = selHolidays,
            currentMonth = dialogDate?.monthValue ?: today.monthValue,
            onDismiss = { vm.clearSelection() }
        )
    }
}