package com.example.calendarapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.calendarapp.data.Holiday

@Composable
fun HolidayDialog(
    holidays: List<Holiday>,
    currentMonth: Int,
    onDismiss: () -> Unit
) {
    val pageSize = 5
    var currentPage by remember { mutableIntStateOf(0) }
    val totalPages = (holidays.size + pageSize - 1) / pageSize

    val majorHoliday = holidays.find { it.emoji != "📅" }

    val dateInfo = if (holidays.isNotEmpty()) {
        val h = holidays.first()
        "${h.day} ${getMonthShort(h.month)}"
    } else ""

    val topEmoji = majorHoliday?.emoji ?: getMonthEmoji(currentMonth)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (holidays.isEmpty()) {
                    Text(getMonthEmoji(currentMonth), fontSize = 64.sp)
                    Spacer(Modifier.height(16.dp))
                    Text("Обычный день", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(8.dp))
                    Text("Загрузка праздников...", style = MaterialTheme.typography.bodyMedium)
                } else {
                    Text(
                        "$topEmoji $dateInfo",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))

                    Text(
                        "Праздников: ${holidays.size}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(16.dp))

                    val start = currentPage * pageSize
                    val end = minOf(start + pageSize, holidays.size)
                    val pageHolidays = holidays.subList(start, end)

                    pageHolidays.forEachIndexed { i, h ->
                        Text(
                            h.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center
                        )
                        if (i < pageHolidays.lastIndex) {
                            Spacer(Modifier.height(8.dp))
                            Divider()
                            Spacer(Modifier.height(8.dp))
                        }
                    }

                    if (totalPages > 1) {
                        Spacer(Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = { if (currentPage > 0) currentPage-- },
                                enabled = currentPage > 0
                            ) {
                                Text("◀ Назад")
                            }

                            Text(
                                "${currentPage + 1}/$totalPages",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            TextButton(
                                onClick = { if (currentPage < totalPages - 1) currentPage++ },
                                enabled = currentPage < totalPages - 1
                            ) {
                                Text("Вперёд ▶")
                            }
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))
                Button(onDismiss, Modifier.fillMaxWidth()) { Text("Закрыть") }
            }
        }
    }
}

private fun getMonthShort(month: Int): String {
    return when (month) {
        1 -> "янв"
        2 -> "фев"
        3 -> "мар"
        4 -> "апр"
        5 -> "мая"
        6 -> "июн"
        7 -> "июл"
        8 -> "авг"
        9 -> "сен"
        10 -> "окт"
        11 -> "ноя"
        12 -> "дек"
        else -> ""
    }
}

private fun getMonthEmoji(month: Int): String {
    return when (month) {
        1 -> "❄️"
        2 -> "💨"
        3 -> "🌱"
        4 -> "🌷"
        5 -> "🌸"
        6 -> "☀️"
        7 -> "🌻"
        8 -> "🍎"
        9 -> "🍂"
        10 -> "🎃"
        11 -> "🌧️"
        12 -> "⛄"
        else -> "📅"
    }
}