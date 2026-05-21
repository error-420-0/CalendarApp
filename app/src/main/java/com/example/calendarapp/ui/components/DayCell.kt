package com.example.calendarapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calendarapp.data.Holiday
import com.example.calendarapp.ui.theme.TodayBlue
import java.time.LocalDate

@Composable
fun DayCell(
    date: LocalDate?,
    isSelected: Boolean,
    holiday: Holiday?,
    today: LocalDate,
    onClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .background(
                when {
                    date == null -> MaterialTheme.colorScheme.surface
                    isSelected -> MaterialTheme.colorScheme.primary
                    holiday != null -> MaterialTheme.colorScheme.primaryContainer
                    else -> MaterialTheme.colorScheme.surface
                }
            )
            .border(
                width = if (date != null && date == today) 2.dp else 0.dp,
                color = TodayBlue,
                shape = CircleShape
            )
            .clickable(enabled = date != null) { date?.let { onClick(it) } },
        contentAlignment = Alignment.Center
    ) {
        if (date != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = date.dayOfMonth.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                if (holiday != null) {
                    Text(
                        text = holiday.emoji,
                        fontSize = if (holiday.name.length > 10) 8.sp else 10.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}