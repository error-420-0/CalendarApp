package com.example.calendarapp.ui.theme

import android.content.Context
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

enum class AccentColor(val displayName: String, val primary: Color, val onPrimary: Color) {
    PURPLE("Фиолетовый", Color(0xFF6650a4), Color.White),
    BLUE("Синий", Color(0xFF1565C0), Color.White),
    GREEN("Зелёный", Color(0xFF2E7D32), Color.White),
    ORANGE("Оранжевый", Color(0xFFE65100), Color.White),
    RED("Красный", Color(0xFFC62828), Color.White),
    PINK("Розовый", Color(0xFFAD1457), Color.White),
    TEAL("Бирюзовый", Color(0xFF00695C), Color.White)
}

object ThemeManager {
    private const val PREFS_NAME = "app_theme"
    private const val KEY_ACCENT = "accent_color"

    var currentAccent by mutableStateOf(AccentColor.PURPLE)
        private set

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val accentName = prefs.getString(KEY_ACCENT, AccentColor.PURPLE.name)
        currentAccent = AccentColor.entries.find { it.name == accentName } ?: AccentColor.PURPLE
    }

    fun setAccent(context: Context, accent: AccentColor) {
        currentAccent = accent
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_ACCENT, accent.name).apply()
    }
}

@Composable
fun CalendarAppTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    // Инициализируем тему при первом запуске
    LaunchedEffect(Unit) {
        ThemeManager.init(context)
    }

    val accent = ThemeManager.currentAccent
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = accent.primary.copy(alpha = 0.8f),
            onPrimary = accent.onPrimary,
            primaryContainer = accent.primary.copy(alpha = 0.3f),
            onPrimaryContainer = accent.primary,
            secondary = accent.primary.copy(alpha = 0.7f),
            tertiary = accent.primary.copy(alpha = 0.6f)
        )
    } else {
        lightColorScheme(
            primary = accent.primary,
            onPrimary = accent.onPrimary,
            primaryContainer = accent.primary.copy(alpha = 0.15f),
            onPrimaryContainer = accent.primary,
            secondary = accent.primary.copy(alpha = 0.8f),
            tertiary = accent.primary.copy(alpha = 0.7f)
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}