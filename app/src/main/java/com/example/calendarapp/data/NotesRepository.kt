package com.example.calendarapp.data

import android.content.Context
import androidx.core.content.edit

class NotesRepository(context: Context) {
    private val prefs = context.getSharedPreferences("calendar_notes", Context.MODE_PRIVATE)

    fun getNote(dateKey: String): String {
        return prefs.getString(dateKey, "") ?: ""
    }

    fun saveNote(dateKey: String, note: String) {
        prefs.edit { putString(dateKey, note) }
    }

    fun deleteNote(dateKey: String) {
        prefs.edit { remove(dateKey) }
    }

    companion object {
        fun dateKey(month: Int, day: Int): String = "${month}_${day}"
    }
}