package com.example.calendarapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calendarapp.data.Holiday

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayDetailScreen(
    holidays: List<Holiday>,
    dateInfo: String,
    topEmoji: String,
    isLoading: Boolean,
    errorMessage: String?,
    isOffline: Boolean,
    onBack: () -> Unit,
    onClearError: () -> Unit
) {
    val pageSize = 5
    var currentPage by remember { mutableIntStateOf(0) }
    val totalPages = (holidays.size + pageSize - 1) / pageSize

    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            kotlinx.coroutines.delay(3000)
            onClearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(dateInfo.ifEmpty { "Праздники" }) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (holidays.isEmpty() && !isLoading) {
                Spacer(Modifier.height(80.dp))
                Text(topEmoji, fontSize = 72.sp)
                Spacer(Modifier.height(24.dp))
                Text("Обычный день", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(12.dp))
                Text("Праздников не найдено", style = MaterialTheme.typography.bodyLarge)
            } else {
                Text(topEmoji, fontSize = 48.sp)

                if (dateInfo.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Text(dateInfo, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(8.dp))
                Text(
                    "Праздников: ${holidays.size}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Ошибка
                if (errorMessage != null) {
                    Spacer(Modifier.height(12.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(if (isOffline) "🌐" else "⚠️", fontSize = 20.sp)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                errorMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }

                // Загрузка
                if (isLoading) {
                    Spacer(Modifier.height(12.dp))
                    LinearProgressIndicator(Modifier.fillMaxWidth())
                    Spacer(Modifier.height(4.dp))
                    Text("Загрузка...", style = MaterialTheme.typography.bodySmall)
                }

                Spacer(Modifier.height(20.dp))

                // Список
                val start = currentPage * pageSize
                val end = minOf(start + pageSize, holidays.size)
                val pageHolidays = holidays.subList(start, end)

                pageHolidays.forEachIndexed { i, h ->
                    Card(
                        Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(
                            h.name,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Пагинация
                if (totalPages > 1) {
                    Spacer(Modifier.height(16.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { if (currentPage > 0) currentPage-- }, enabled = currentPage > 0) {
                            Text("◀ Назад")
                        }
                        Text("${currentPage + 1}/$totalPages")
                        TextButton(onClick = { if (currentPage < totalPages - 1) currentPage++ }, enabled = currentPage < totalPages - 1) {
                            Text("Вперёд ▶")
                        }
                    }
                }
            }
        }
    }
}

fun getMonthShort(month: Int): String {
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

fun getMonthEmoji(month: Int): String {
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