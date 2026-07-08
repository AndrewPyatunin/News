package com.andreich.news.ui.core

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.andreich.news.ext.TextHeader

@Composable
fun AnimatedButton(modifier: Modifier = Modifier, text: String, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed = interactionSource.collectIsPressedAsState()
    val sizeScale by animateFloatAsState(if (isPressed.value) 0.8f else 1f)

    Button(
        onClick = onClick,
        modifier = modifier
            .indication(interactionSource, LocalIndication.current)
            .clickable { isPressed.value }
            .padding(16.dp)
            .wrapContentSize()
            .graphicsLayer(
                scaleX = sizeScale,
                scaleY = sizeScale
            ),
        shape = RoundedCornerShape(10),
        elevation = ButtonDefaults.buttonElevation(if (isPressed.value) 10.dp else 0.dp),
        interactionSource = interactionSource
    ) {
        TextHeader(text)
    }
}