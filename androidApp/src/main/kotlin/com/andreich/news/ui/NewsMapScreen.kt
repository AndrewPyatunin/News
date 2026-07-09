package com.andreich.news.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andreich.news.ext.KEY_NEWS_IDS
import com.andreich.news.ext.toFeature
import com.andreich.news.presentation.core.UiMessage
import com.andreich.news.presentation.newsmap.NewsMapEvent
import com.andreich.news.presentation.newsmap.NewsMapIntent
import com.andreich.news.presentation.newsmap.NewsMapState
import com.andreich.news.presentation.newsmap.NewsMapViewModel
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.maplibre.android.geometry.LatLng
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.expressions.dsl.Feature.has
import org.maplibre.compose.expressions.dsl.asString
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.feature
import org.maplibre.compose.expressions.dsl.format
import org.maplibre.compose.expressions.dsl.not
import org.maplibre.compose.expressions.dsl.span
import org.maplibre.compose.layers.CircleLayer
import org.maplibre.compose.layers.SymbolLayer
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.GeoJsonOptions
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.util.ClickResult
import org.maplibre.geojson.FeatureCollection
import org.maplibre.spatialk.geojson.Feature.Companion.getStringProperty
import org.maplibre.spatialk.geojson.Position

@Composable
fun NewsMapRoute(
    snackbarHostState: SnackbarHostState,
    onNavigateToNewsDetails: (Int) -> Unit,
    onNavigateToNewsCityList: (List<Int>) -> Unit,
    isEnglish: Boolean = true
) {
    val viewModel: NewsMapViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.sendIntent(NewsMapIntent.StartObserving(isEnglish))
        viewModel.events.collect {
            when (it) {
                is NewsMapEvent.NavigateToNews -> {
                    onNavigateToNewsDetails(it.newsId)
                }

                is NewsMapEvent.NavigateToNewsCityList -> {
                    onNavigateToNewsCityList(it.ids)
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        viewModel.messages.collect {
            when (it) {
                is UiMessage.ShowError -> snackbarHostState.showSnackbar(it.message)
                is UiMessage.ShowSuccess -> snackbarHostState.showSnackbar(it.message)
            }
        }
    }
    NewsMapScreen(state) {
        viewModel.sendIntent(NewsMapIntent.ClickItem(it))
    }
}

@Composable
fun NewsMapScreen(state: NewsMapState, onMarkerClick: (List<Int>) -> Unit) {
    val features = remember(state.clusterItems) {
        FeatureCollection.fromFeatures(
            state.clusterItems.map { it.toFeature() }
        )
    }
    val moscow = LatLng(55.7558, 37.6173)
    val cameraState = rememberCameraState(
        firstPosition = CameraPosition(
            target = Position(
                moscow.latitude,
                moscow.longitude
            ), zoom = 3.0
        )
    )
    MaplibreMap(
        modifier = Modifier.fillMaxSize(),
        cameraState = cameraState,
        baseStyle = BaseStyle.Uri(
            "https://tiles.openfreemap.org/styles/liberty"
        ),
    ) {
        val scope = rememberCoroutineScope()
        val source = rememberGeoJsonSource(
            data = GeoJsonData.JsonString(features.toJson()),

            options = GeoJsonOptions(
                cluster = true,
                clusterRadius = 60,
                clusterMaxZoom = 14
            )
        )
        CircleLayer(
            id = "markers",
            source = source,
            filter = !(has("point_count")),
            color = const(Color.Red),
            radius = const(8.dp),
            onClick = { features ->
                val feature = features.firstOrNull()
                    ?: return@CircleLayer ClickResult.Consume
                val ids =
                    feature.getStringProperty(KEY_NEWS_IDS)?.trim()
                        ?.split(".")?.mapNotNull { it.toIntOrNull() }
                        ?: emptyList()
                onMarkerClick(ids)

                ClickResult.Consume
            }
        )

        CircleLayer(
            id = "clusters",
            source = source,
            filter = has("point_count"),
            color = const(Color.Blue),
            radius = const(20.dp),
            onClick = { features ->
                scope.launch {
                    cameraState.animateTo(finalPosition = cameraState.position.copy(zoom = cameraState.position.zoom.plus(1.0)))
                }

                ClickResult.Consume
            }
        )

        SymbolLayer(
            id = "cluster-count",
            textFont = const(listOf(const("Noto Sans Regular"))),
            source = source,
            filter = has("point_count"),
            textField = format(span(feature["point_count_abbreviated"].asString())),
            textColor = const(Color.White),
            textSize = const(12.sp),
            onClick = {
                scope.launch {
                    cameraState.animateTo(finalPosition = cameraState.position.copy(zoom = cameraState.position.zoom.plus(1.0)))
                }

                ClickResult.Consume
            }
        )
    }
}