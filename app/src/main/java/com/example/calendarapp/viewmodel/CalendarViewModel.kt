package com.example.calendarapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.calendarapp.data.CalendRuParser
import com.example.calendarapp.data.Holiday
import com.example.calendarapp.data.HolidaysRepository
import com.example.calendarapp.data.NotesRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.temporal.ChronoUnit

class CalendarViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = HolidaysRepository()
    private val notesRepo = NotesRepository(application)

    val currentYearMonth = MutableStateFlow(YearMonth.now())
    val selectedDate = MutableStateFlow<LocalDate?>(null)
    val selectedHolidays = MutableStateFlow<List<Holiday>>(emptyList())
    val daysInMonth = MutableStateFlow<List<LocalDate?>>(emptyList())
    val today = MutableStateFlow(LocalDate.now())
    val showDialog = MutableStateFlow(false)
    val dialogDate = MutableStateFlow<LocalDate?>(null)
    val isLoading = MutableStateFlow(false)
    val errorMessage = MutableStateFlow<String?>(null)
    val isOffline = MutableStateFlow(false)
    val currentNote = MutableStateFlow("")
    val showNoteDialog = MutableStateFlow(false)

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
                if (YearMonth.now() != currentYearMonth.value) {
                    currentYearMonth.value = YearMonth.now()
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
        dialogDate.value = date

        val major = listOfNotNull(repo.getMajorHoliday(date.monthValue, date.dayOfMonth))
        selectedHolidays.value = major
        showDialog.value = true

        // Загружаем заметку
        val dateKey = NotesRepository.dateKey(date.monthValue, date.dayOfMonth)
        currentNote.value = notesRepo.getNote(dateKey)

        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            isOffline.value = false

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

                isLoading.value = false

            } catch (_: Exception) {
                isLoading.value = false
                errorMessage.value = "Ошибка загрузки"
            }
        }
    }

    fun getMajorHoliday(date: LocalDate): Holiday? {
        return repo.getMajorHoliday(date.monthValue, date.dayOfMonth)
    }

    // Методы для заметок
    fun onNoteChanged(note: String) {
        currentNote.value = note
    }

    fun saveNote() {
        val date = dialogDate.value ?: return
        val dateKey = NotesRepository.dateKey(date.monthValue, date.dayOfMonth)
        if (currentNote.value.isBlank()) {
            notesRepo.deleteNote(dateKey)
        } else {
            notesRepo.saveNote(dateKey, currentNote.value)
        }
        showNoteDialog.value = false
    }

    fun openNoteEditor() {
        showNoteDialog.value = true
    }

    fun closeNoteEditor() {
        showNoteDialog.value = false
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
        isLoading.value = false
        errorMessage.value = null
        isOffline.value = false
    }

    fun clearError() {
        errorMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
    }
}