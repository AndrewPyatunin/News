package com.andreich.news

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import com.andreich.news.presentation.core.UiMessage
import com.andreich.news.presentation.usersettings.SettingsIntent
import com.andreich.news.presentation.usersettings.SettingsViewModel
import com.andreich.news.ui.MainScreen
import com.andreich.news.ui.theme.AppTheme
import org.koin.compose.viewmodel.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: SettingsViewModel = koinViewModel()
            val settings = viewModel.state.collectAsState()
            val snackbarHostState = remember { SnackbarHostState() }

            AppTheme(settings.value.userSettings?.darkTheme == true) {
                MainScreen()
            }
            LaunchedEffect(viewModel) {
                viewModel.sendIntent(SettingsIntent.LoadUserSettings)
                val value = viewModel.state.value.userSettings?.country?.name
                viewModel.messages.collect {
                    Log.d("Value", value.orEmpty())
                    when (it) {
                        is UiMessage.ShowError -> snackbarHostState.showSnackbar(it.message)
                        is UiMessage.ShowSuccess -> snackbarHostState.showSnackbar(it.message)
                    }
                }
            }
        }
    }
}