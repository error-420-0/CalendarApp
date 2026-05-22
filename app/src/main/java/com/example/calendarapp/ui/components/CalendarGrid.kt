package com.example.calendarapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.calendarapp.data.Holiday
import java.time.LocalDate

@Composable
fun CalendarGrid(
    days: List<LocalDate?>,
    selectedDate: LocalDate?,
    today: LocalDate,
    getHoliday: (LocalDate) -> Holiday?,
    onDateClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf("Пн","Вт","Ср","Чт","Пт","Сб","Вс").forEach {
                Text(it, Modifier.weight(1f), textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
            }
        }
        Spacer(Modifier.height(8.dp))

        days.chunked(7).forEach { week ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                week.forEach { date ->
                    DayCell(date, date == selectedDate, date?.let { getHoliday(it) }, today, onDateClick, Modifier.weight(1f))
                }
                repeat(7 - week.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}