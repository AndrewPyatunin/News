package com.andreich.news

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.andreich.news.presentation.SettingsViewModel
import com.andreich.news.ui.MainScreen
import com.andreich.news.ui.theme.AppTheme
import org.koin.compose.viewmodel.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: SettingsViewModel = koinViewModel()
            val settings by viewModel.settings.collectAsState()
            val errors = viewModel.errors.collectAsState()
            AppTheme(settings.darkTheme) {
                MainScreen()
            }
            LaunchedEffect(Unit) {
                val value = viewModel.settings.value.language.name
                Log.d("Locale", value)
            }
        }
    }
}