package com.example.calendarapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calendarapp.data.CalendRuParser
import com.example.calendarapp.data.Holiday
import com.example.calendarapp.data.HolidaysRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.temporal.ChronoUnit

class CalendarViewModel : ViewModel() {
    private val repo = HolidaysRepository()

    val currentYearMonth = MutableStateFlow(YearMonth.now())
    val selectedDate = MutableStateFlow<LocalDate?>(null)
    val selectedHolidays = MutableStateFlow<List<Holiday>>(emptyList())
    val daysInMonth = MutableStateFlow<List<LocalDate?>>(emptyList())
    val today = MutableStateFlow(LocalDate.now())
    val showDialog = MutableStateFlow(false)
    val dialogDate = MutableStateFlow<LocalDate?>(null) // Для диалога

    init {
        updateDays()
        startMidnightTimer()
    }

    private fun startMidnightTimer() {
        viewModelScope.launch {
            while (isActive) {
                val now = LocalDateTime.now()
                val midnight = now.toLocalDate().plusDays(1).atStartOfDay()
                delay(ChronoUnit.MILLIS.between(now, midnight) + 1000)
                today.value = LocalDate.now()
                val newMonth = YearMonth.now()
                if (newMonth != currentYearMonth.value) {
                    currentYearMonth.value = newMonth
                    updateDays()
                }
            }
        }
    }

    fun nextMonth() {
        currentYearMonth.value = currentYearMonth.value.plusMonths(1)
        updateDays()
    }

    fun previousMonth() {
        currentYearMonth.value = currentYearMonth.value.minusMonths(1)
        updateDays()
    }

    fun onDateClick(date: LocalDate) {
        selectedDate.value = date
        dialogDate.value = date // Сохраняем дату для диалога

        val major = listOfNotNull(repo.getMajorHoliday(date.monthValue, date.dayOfMonth))
        selectedHolidays.value = major
        showDialog.value = true

        viewModelScope.launch {
            try {
                val parsed = withContext(Dispatchers.IO) {
                    CalendRuParser.getHolidaysForDay(date.monthValue, date.dayOfMonth)
                }

                if (parsed.isNotEmpty()) {
                    val all = mutableListOf<Holiday>()
                    all.addAll(major)
                    parsed.forEach { h ->
                        if (all.none { it.name.equals(h.name, ignoreCase = true) }) {
                            all.add(h)
                        }
                    }
                    selectedHolidays.value = all
                }
            } catch (e: Exception) {
                // Игнорируем ошибки парсинга
            }
        }
    }

    fun getMajorHoliday(date: LocalDate): Holiday? {
        return repo.getMajorHoliday(date.monthValue, date.dayOfMonth)
    }

    private fun updateDays() {
        val ym = currentYearMonth.value
        val list = mutableListOf<LocalDate?>()
        val first = ym.atDay(1)
        val last = ym.atEndOfMonth()
        repeat(first.dayOfWeek.value - 1) { list.add(null) }
        for (d in 1..last.dayOfMonth) list.add(ym.atDay(d))
        daysInMonth.value = list
    }

    fun clearSelection() {
        selectedDate.value = null
        selectedHolidays.value = emptyList()
        showDialog.value = false
    }

    override fun onCleared() {
        super.onCleared()
    }
}