package com.andreich.news.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@Composable
fun NewsMapRoute() {
    Box(modifier = Modifier.fillMaxSize()) {
        Text("Map", fontSize = 26.sp)
    }
}