package com.andreich.news.ext

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.andreich.news.domain.model.News

@Composable
fun NewsItem(news: News, onClickNewsListener: (News) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth().padding(4.dp)
            .clickable(onClick = {
                onClickNewsListener(news)
            }),
        border = BorderStroke(1.dp, color = Color.Black),
        shape = CardDefaults.elevatedShape,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box(modifier = Modifier
                .weight(1f)
                .shimmerLoading(1500)) {
                AsyncImage(
                    model = news.imageUrl.imageBuild(LocalContext.current),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier,
                    contentDescription = null,
                )
            }

            Column(modifier = Modifier.padding(horizontal = 4.dp).weight(2f)) {
                Text(text = news.title, fontSize = 18.sp, fontFamily = FontFamily.Serif)
                Spacer(Modifier.size(6.dp))
                Text(text = news.description, fontSize = 14.sp)
            }
        }
    }
}

private fun String.imageBuild(context: Context): ImageRequest {
    return ImageRequest.Builder(context)
        .data(this)
        .crossfade(true)
        .build()
}