package com.andreich.news.ext

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

@Composable
fun MenuPopUpItem(
    modifier: Modifier = Modifier
        .padding(top = 80.dp),
    alignment: Alignment = Alignment.TopCenter,
    onDismiss: () -> Unit,
    visible: Boolean,
    content: @Composable () -> Unit,
) {
    val transitionState = remember {
        MutableTransitionState(false)
    }

    transitionState.targetState = visible
    if (transitionState.currentState || transitionState.targetState) {
        Popup(
            alignment = alignment, onDismissRequest = onDismiss,
            properties = PopupProperties(
                focusable = true
            )
        ) {
            AnimatedVisibility(
                visibleState = transitionState,
                enter = fadeIn(animationSpec = tween(350)) + slideInVertically(animationSpec = tween(350)),
                exit = fadeOut(animationSpec = tween(350)) + slideOutVertically(animationSpec = tween(350))
            ) {
                Card(
                    modifier = modifier
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
    }
}

@Composable
fun TextHeader(text: String) {
    Text(
        text = text,
        fontSize = 22.sp,
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth(),
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
