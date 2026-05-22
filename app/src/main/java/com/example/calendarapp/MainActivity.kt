package com.example.calendarapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.calendarapp.ui.screens.CalendarScreen
import com.example.calendarapp.ui.screens.SettingsScreen
import com.example.calendarapp.ui.theme.CalendarAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalendarAppTheme {
                var showSettings by remember { mutableStateOf(false) }

                Surface(modifier = Modifier.fillMaxSize()) {
                    if (showSettings) {
                        SettingsScreen(onBack = { showSettings = false })
                    } else {
                        CalendarScreen(onSettingsClick = { showSettings = true })
                    }
                }
            }
        }
    }
}