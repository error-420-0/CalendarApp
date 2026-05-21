package com.example.calendarapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val repository = HolidaysRepository()

    private val _currentYearMonth = MutableStateFlow(YearMonth.now())
    val currentYearMonth: StateFlow<YearMonth> = _currentYearMonth

    private val _selectedDate = MutableStateFlow<LocalDate?>(null)
    val selectedDate: StateFlow<LocalDate?> = _selectedDate

    private val _selectedHoliday = MutableStateFlow<Holiday?>(null)
    val selectedHoliday: StateFlow<Holiday?> = _selectedHoliday

    private val _daysInMonth = MutableStateFlow<List<LocalDate?>>(emptyList())
    val daysInMonth: StateFlow<List<LocalDate?>> = _daysInMonth

    private val _today = MutableStateFlow(LocalDate.now())
    val today: StateFlow<LocalDate> = _today

    private var midnightJob: Job? = null

    init {
        updateDaysInMonth()
        startMidnightUpdate()
    }

    private fun startMidnightUpdate() {
        midnightJob?.cancel()
        midnightJob = viewModelScope.launch {
            while (isActive) {
                // Вычисляем время до следующей полуночи
                val now = LocalDateTime.now()
                val midnight = now.toLocalDate().plusDays(1).atStartOfDay()
                val millisUntilMidnight = ChronoUnit.MILLIS.between(now, midnight)

                // Ждём до полуночи + 1 секунда для надёжности
                delay(millisUntilMidnight + 1000)

                // Обновляем сегодняшнюю дату
                _today.value = LocalDate.now()

                // Если наступил новый месяц - обновляем календарь
                val newYearMonth = YearMonth.now()
                if (newYearMonth != _currentYearMonth.value) {
                    _currentYearMonth.value = newYearMonth
                    updateDaysInMonth()
                }
            }
        }
    }

    fun nextMonth() {
        _currentYearMonth.value = _currentYearMonth.value.plusMonths(1)
        updateDaysInMonth()
    }

    fun previousMonth() {
        _currentYearMonth.value = _currentYearMonth.value.minusMonths(1)
        updateDaysInMonth()
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        _selectedHoliday.value = repository.getHoliday(date.monthValue, date.dayOfMonth)
    }

    fun getHolidayForDate(date: LocalDate): Holiday? {
        return repository.getHoliday(date.monthValue, date.dayOfMonth)
    }

    private fun updateDaysInMonth() {
        val yearMonth = _currentYearMonth.value
        val days = mutableListOf<LocalDate?>()

        val firstDayOfMonth = yearMonth.atDay(1)
        val lastDayOfMonth = yearMonth.atEndOfMonth()

        val dayOfWeek = firstDayOfMonth.dayOfWeek.value
        for (i in 1 until dayOfWeek) {
            days.add(null)
        }

        for (day in 1..lastDayOfMonth.dayOfMonth) {
            days.add(yearMonth.atDay(day))
        }

        _daysInMonth.value = days
    }

    fun clearSelection() {
        _selectedDate.value = null
        _selectedHoliday.value = null
    }

    override fun onCleared() {
        super.onCleared()
        midnightJob?.cancel()
    }
}