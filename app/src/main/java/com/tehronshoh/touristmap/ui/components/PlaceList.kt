package com.tehronshoh.touristmap.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.tehronshoh.touristmap.R
import com.tehronshoh.touristmap.extensions.distance
import com.tehronshoh.touristmap.model.Place
import com.tehronshoh.touristmap.remote.RetrofitClient
import com.tehronshoh.touristmap.ui.tool.LocalUserCurrentPosition
import com.yandex.mapkit.geometry.Point

@Composable
fun PlaceList(
    places: List<Place>,
    onPlaceSelected: (Place) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.White)
            .padding(top = 12.dp)
    ) {
        items(places) { place ->
            PlaceListItem(place = place, onPlaceSelected = onPlaceSelected)

            val height = if (places.last() == place) 70.dp else 12.dp
            Spacer(modifier = Modifier.height(height))
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PlaceListItem(place: Place, onPlaceSelected: (Place) -> Unit) {
    val distance = LocalUserCurrentPosition.current?.let {
        "Расстояние: " + it.distance(Point(place.latitude, place.longitude))
    } ?: "Ваша локация не доступно!"

    Row(
        modifier = Modifier
            .padding(horizontal = 30.dp)
            .fillMaxWidth()
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(10.dp))
            .clickable {
                onPlaceSelected(place)
            }
            .background(color = Color.White)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        GlideImage(
            model = place.images.firstOrNull()?.let { RetrofitClient.BASE_URL + it } ?: R.drawable.auth_image,
            contentDescription = place.name,
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(5.dp)),
            contentScale = ContentScale.Crop
        ) {
            it.load(place.images.firstOrNull()?.let { url -> RetrofitClient.BASE_URL + url } ?: R.drawable.auth_image)
        }
        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = place.name,
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(resId = R.font.montserrat_600)),
                style = TextStyle(
                    color = colorResource(id = R.color.black),
                    fontWeight = FontWeight.W500
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = distance,
                fontSize = 10.sp,
                fontFamily = FontFamily(Font(resId = R.font.montserrat_600)),
                style = TextStyle(
                    color = colorResource(id = R.color.black),
                    fontWeight = FontWeight.W400
                ),
                modifier = Modifier.alpha(0.5f)
            )
        }
        val favIcon = if(place.isFavorite == 1)  R.drawable.selected_star else R.drawable.un_star
        Image(
            imageVector = ImageVector.vectorResource(id = favIcon),
            contentDescription = "like",
            modifier = Modifier.padding(end = 12.dp)
        )
    }
}

@Preview
@Composable
private fun PlaceListPreview() {
    //PlaceList(places = MainViewModel().listOfPlace.value, onPlaceSelected = {})
}