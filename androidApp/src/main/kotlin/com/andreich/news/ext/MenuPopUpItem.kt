package com.andreich.news.ext

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

@Composable
fun MenuPopUpItem(alignment: Alignment = Alignment.TopCenter, onDismiss: () -> Unit, content: @Composable () -> Unit) {
    Popup(alignment = alignment, onDismissRequest = { onDismiss() },
        properties = PopupProperties(
            focusable = true
        )

    ) {
        Card(
            modifier = Modifier
                .padding(top = 80.dp)
                .fillMaxWidth()
                .height(300.dp),
            elevation = CardDefaults.cardElevation(4.dp, focusedElevation = 8.dp),
            shape = ShapeDefaults.Medium,
            border = BorderStroke(2.dp, color = MaterialTheme.colorScheme.onPrimaryFixed)
        ) {
            content()
        }
    }
}

@Composable
fun TextHeader(text: String) {
    Text(
        text = text,
        fontSize = 22.sp,
        modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth(),
        textAlign = TextAlign.Left
    )
}

@Composable
fun TextContent(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        modifier = Modifier.padding(horizontal = 8.dp)
    )
}
