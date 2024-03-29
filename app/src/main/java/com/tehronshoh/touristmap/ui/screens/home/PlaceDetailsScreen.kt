package com.tehronshoh.touristmap.ui.screens.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.tehronshoh.touristmap.R
import com.tehronshoh.touristmap.model.Place
import com.tehronshoh.touristmap.model.RouteDrawingType
import com.tehronshoh.touristmap.model.RouteSettings
import com.tehronshoh.touristmap.model.RouteType
import com.tehronshoh.touristmap.model.TransportType
import com.tehronshoh.touristmap.remote.RetrofitClient
import com.tehronshoh.touristmap.ui.components.RadioGroup

@Composable
fun PlaceDetailsScreen(
    place: Place,
    isBackButtonShow: Boolean = true,
    showOnMap: (place: Place, createRoute: RouteSettings?) -> Unit,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        TopBar((place.isFavorite == 1), isBackButtonShow, onNavigateBack)

        ScreenContent(place, !isBackButtonShow, showOnMap)
    }
}

@Composable
fun ScreenContent(
    place: Place,
    asBottomSheetShowing: Boolean,
    showOnMap: (place: Place, createRoute: RouteSettings?) -> Unit
) {
    val scrollState = rememberScrollState()
    var showRouteSettings by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = scrollState)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        ) {
            if (!asBottomSheetShowing) Button(
                onClick = {
                    showOnMap(place, null)
                }, colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.primary)
                ), modifier = Modifier
                    .padding(bottom = 12.dp, end = 4.dp)
                    .weight(1f)
            ) {
                Text("Показать на карте", fontSize = 12.sp, textAlign = TextAlign.Center)
            }

            val modifier =
                if (showRouteSettings) Modifier
                    .padding(bottom = 12.dp, start = 4.dp)
                    .weight(1f)
                    .alpha(0.5f)
                else Modifier
                    .padding(bottom = 12.dp, start = 4.dp)
                    .weight(1f)

            Button(
                onClick = {
                    showRouteSettings = !showRouteSettings
                }, colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.primary)
                ), modifier = modifier
            ) {
                Text("Построить маршрут", fontSize = 12.sp, textAlign = TextAlign.Center)
            }
        }
        if (showRouteSettings) RouteSettingsScreen(place = place, onRouteSettingsSelected = {
            showOnMap(place, it)
        })
        Text(
            text = place.name,
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(resId = R.font.montserrat_600)),
            style = TextStyle(color = Color.Black, fontWeight = FontWeight.W500),
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(horizontal = 30.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = place.description,
            fontSize = 12.sp,
            fontFamily = FontFamily(Font(resId = R.font.montserrat_600)),
            style = TextStyle(color = Color.Black, fontWeight = FontWeight.W400),
            textAlign = TextAlign.Start,
            modifier = Modifier
                .padding(horizontal = 30.dp)
                .alpha(0.5f)
        )
        Spacer(modifier = Modifier.height(30.dp))
        ListOfImages(place.images)
        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun RouteSettingsScreen(place: Place, onRouteSettingsSelected: (RouteSettings) -> Unit) {
    var routeType by rememberSaveable { mutableStateOf(RouteType.ALL) }
    var transportType by rememberSaveable { mutableStateOf(TransportType.WALKING) }
    var drawingType by rememberSaveable { mutableStateOf(RouteDrawingType.CLEAR) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .background(color = colorResource(id = R.color.primary_100)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Выберите настройки маршрута:", color = Color.Black, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(8.dp))

        RadioGroup(selectedOption = routeType.label,
            options = listOf(RouteType.ALL.label, RouteType.SINGLE.label),
            onOptionSelected = { selectedType ->
                routeType = when (selectedType) {
                    RouteType.ALL.label -> RouteType.ALL
                    else -> RouteType.SINGLE
                }
            })
        Spacer(modifier = Modifier.height(12.dp))
        RadioGroup(selectedOption = transportType.label, options = listOf(
            TransportType.WALKING.label, TransportType.CAR.label, TransportType.BICYCLE.label
        ), onOptionSelected = { selectedType ->
            transportType = when (selectedType) {
                TransportType.WALKING.label -> TransportType.WALKING
                TransportType.BICYCLE.label -> TransportType.BICYCLE
                else -> TransportType.CAR
            }
        })
        Spacer(modifier = Modifier.height(12.dp))
        RadioGroup(selectedOption = drawingType.label, options = listOf(
            RouteDrawingType.CLEAR.label, RouteDrawingType.ADD.label
        ), onOptionSelected = { selectedType ->
            drawingType = when (selectedType) {
                RouteDrawingType.CLEAR.label -> RouteDrawingType.CLEAR
                else -> RouteDrawingType.ADD
            }
        })
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = {
                val routeSettings = RouteSettings(
                    listOf(place),
                    routeType,
                    transportType,
                    drawingType
                )
                onRouteSettingsSelected(routeSettings)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.primary)
            ), modifier = Modifier
                .padding(bottom = 12.dp, end = 4.dp)
                .wrapContentWidth()
        ) {
            Text("Построить маршрут", fontSize = 12.sp, textAlign = TextAlign.Center)
        }
    }
}


@Composable
fun ListOfImages(images: List<String>) {
    val listStateRemember = rememberLazyListState()

    LazyRow(state = listStateRemember) {
        items(images) { url ->
            if (images.first() == url) Spacer(modifier = Modifier.width(30.dp))

            Log.d("TAG_DETAILS", "ListOfImages: ${RetrofitClient.BASE_URL + url}")
            AsyncImage(
                model = RetrofitClient.BASE_URL + url,
                contentDescription = "image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(
                    120.dp,
                    100.dp
                )
            )
            Spacer(modifier = Modifier.width(20.dp))
        }
    }
}

@Composable
private fun TopBar(isFav: Boolean, isBackButtonShow: Boolean, onNavigateBack: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(horizontal = 30.dp)
            .padding(top = 25.dp, bottom = 12.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isBackButtonShow)
            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.back_button),
                contentDescription = "Back Button",
                modifier = Modifier.clickable { onNavigateBack() }
            )
        Text(
            text = stringResource(id = R.string.description),
            fontSize = 18.sp,
            fontFamily = FontFamily(Font(resId = R.font.montserrat_600)),
            style = TextStyle(color = Color.Black, fontWeight = FontWeight.W500),
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        val favIcon = if (isFav) R.drawable.selected_star else R.drawable.un_star
        Image(
            imageVector = ImageVector.vectorResource(id = favIcon),
            contentDescription = "Favorite Button"
        )
    }
}

@Preview
@Composable
private fun PlaceDetailsScreenPreview() {
    PlaceDetailsScreen(
        place = Place(
            2,
            "Парк национального флага",
            38.58121398293717,
            68.78074208988657,
            "",
            "", 0,
            listOf()
        ),
        showOnMap = {_, _ ->},
        onNavigateBack = {}
    )
}