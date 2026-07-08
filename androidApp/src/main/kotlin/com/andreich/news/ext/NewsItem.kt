package com.andreich.news.ext

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.andreich.news.R
import com.andreich.news.presentation.core.NewsArticle
import com.andreich.news.ui.core.ImageFailureCache
import com.andreich.news.ui.core.ImageRequestFactory
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun NewsItem(news: NewsArticle, onClickNewsListener: () -> Unit) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable(onClick = {
                onClickNewsListener()
            }),
        border = BorderStroke(1.dp, color = Color.Black),
        shape = CardDefaults.elevatedShape,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        val isLoading = rememberSaveable { mutableStateOf(true) }
        val imageLoader: ImageLoader = koinInject()
        val imageRequestFactory = ImageRequestFactory(context)
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(150.dp, 150.dp)
                    .weight(1f)
                    .shimmerLoading(isLoading.value)
            ) {
                if (news.imageUrl != "") {
                    MyAsyncImage(news.imageUrl, imageRequestFactory, imageLoader, isLoading)
                } else {
                    Image(
                        painter = painterResource(R.drawable.news_placeholder),
                        contentDescription = null,
                        contentScale = ContentScale.FillWidth
                    )
                    isLoading.value = false
                }

            }

            Column(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .weight(2f)
            ) {
                Text(
                    text = news.title,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Serif
                )
                Spacer(Modifier.size(6.dp))
                Text(
                    text = news.description,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 5,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun String.imageBuild(context: Context): ImageRequest {
    return remember(this) {
        ImageRequest.Builder(context)
            .data(this)
            .build()
    }
}
@Composable
private fun MyAsyncImage(
    url: String,
    imageRequestFactory: ImageRequestFactory,
    imageLoader: ImageLoader,
    isLoading: MutableState<Boolean>
) {
    val request: ImageRequest = remember(url) { imageRequestFactory.create(url) }
    var loaded by remember(url) {
        mutableStateOf(false)
    }
    LaunchedEffect(loaded) {
        if (loaded) {
            delay(200.milliseconds)
            isLoading.value = false
        }
    }
    if (ImageFailureCache.isBlocked(url)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(R.drawable.news_placeholder),
                contentDescription = null,
                contentScale = ContentScale.FillWidth
            )
            isLoading.value = false
        }
    } else {
        AsyncImage(
            model = request,
            imageLoader = imageLoader,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth(),
            error = painterResource(R.drawable.news_placeholder),
            contentDescription = null,
            onError = {
                isLoading.value = false
                loaded = false
            },
            onSuccess = {
                loaded = true
            },
            onLoading = {
                isLoading.value = true
                loaded = false
            }
        )
    }
}