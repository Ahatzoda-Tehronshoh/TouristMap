package com.tehronshoh.touristmap.ui.screens.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.tehronshoh.touristmap.R
import com.tehronshoh.touristmap.extensions.distance
import com.tehronshoh.touristmap.model.Filter
import com.tehronshoh.touristmap.model.NetworkResult
import com.tehronshoh.touristmap.model.Place
import com.tehronshoh.touristmap.ui.tool.LocalFilteredPlaces
import com.tehronshoh.touristmap.ui.tool.LocalUserCurrentPosition
import com.tehronshoh.touristmap.viewmodel.MainViewModel
import com.yandex.mapkit.geometry.Point

@Composable
fun SearchScreen(
    searchingText: String,
    modifier: Modifier = Modifier,
    onSearchingTextChange: (String) -> Unit,
    onSearchingCancel: () -> Unit,
    choosingFilter: Filter,
    onFilterChange: (Filter) -> Unit
) {
    var dropDownPosition by remember {
        mutableStateOf(Offset(x = 600f, y = 10f))
    }

    var expanded by rememberSaveable {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.white)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 30.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (searchingText.isNotEmpty()) Image(imageVector = ImageVector.vectorResource(id = R.drawable.back_button),
                contentDescription = "filter",
                modifier = Modifier
                    .padding(end = 20.dp)
                    .clickable {
                        onSearchingCancel()
                    })

            TextField(
                value = searchingText,
                onValueChange = { onSearchingTextChange(it) },
                placeholder = {
                    Text(
                        text = "Введите название",
                        fontFamily = FontFamily(Font(resId = R.font.montserrat_600)),
                        fontSize = 12.sp,
                        style = TextStyle(color = Color.Black, fontWeight = FontWeight.W400),
                        modifier = Modifier.alpha(0.4f)
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.search_icon),
                        contentDescription = "email",
                        tint = colorResource(id = R.color.primary),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                singleLine = true,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .border(
                        width = 1.dp,
                        color = colorResource(id = R.color.black_10_alpha),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 12.dp)
                    .weight(1f)
                    .height(50.dp)
                    .background(color = colorResource(id = R.color.white))
            )

            Image(imageVector = ImageVector.vectorResource(id = R.drawable.filter_icon),
                contentDescription = "filter",
                modifier = Modifier
                    .padding(start = 20.dp)
                    .clickable {
                        expanded = !expanded
                    }
                    .onPlaced {
                        dropDownPosition = Offset(
                            it.positionInParent().x - 275.0f, 10f
                        )
                        Log.d("TAG_SEARCH", "SearchScreen: $dropDownPosition")
                    })

            DropdownMenu(expanded = expanded,
                onDismissRequest = { expanded = false },
                offset = DpOffset(x = with(LocalDensity.current) { dropDownPosition.x.toDp() },
                    y = with(LocalDensity.current) { dropDownPosition.y.toDp() }),
                modifier = Modifier
                    .selectableGroup()
                    .shadow(elevation = 0.dp)
                    .border(
                        width = 1.dp,
                        color = colorResource(id = R.color.black_10_alpha),
                        shape = RoundedCornerShape(0.dp)
                    )
                    .background(color = Color.White, shape = RoundedCornerShape(0.dp))
                    .height(IntrinsicSize.Min)) {
                Filter.values().forEach {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier
                            .fillMaxSize()
                            .shadow(elevation = 0.dp)
                    ) {
                        val selected = (choosingFilter == it)

                        val radioButtonColor =
                            if (selected) colorResource(id = R.color.primary) else colorResource(
                                id = R.color.black
                            )

                        val radioButtonModifier =
                            if (selected) Modifier.height(20.dp) else Modifier
                                .height(20.dp)
                                .alpha(0.2f)

                        RadioButton(
                            selected = selected,
                            onClick = {
                                if (!selected) {
                                    expanded = false
                                    onFilterChange(it)
                                }
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = radioButtonColor),
                            modifier = radioButtonModifier
                        )

                        val textColor =
                            if (selected) colorResource(id = R.color.primary) else colorResource(
                                id = R.color.black
                            )

                        val textModifier = if (selected) Modifier.padding(end = 8.dp)
                        else Modifier
                            .alpha(0.5f)
                            .padding(end = 8.dp)
                            .clickable {
                                expanded = false
                                onFilterChange(it)
                            }

                        Text(
                            text = stringResource(id = it.label),
                            fontSize = 10.sp,
                            fontFamily = FontFamily(Font(resId = R.font.montserrat_600)),
                            style = TextStyle(color = textColor, fontWeight = FontWeight.W500),
                            modifier = textModifier
                        )
                    }
                    if (Filter.values().last() != it) Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        Box(
            modifier = Modifier
                .background(color = Color.White)
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            when (val result = LocalFilteredPlaces.current) {
                is NetworkResult.Loading -> {
                    CircularProgressIndicator(
                        color = colorResource(id = R.color.primary),
                        modifier = Modifier.padding(bottom = 40.dp)
                    )
                }

                is NetworkResult.Success -> {
                    PlaceList(
                        places = result.data!!,
                        onPlaceSelected = { }
                    )
                }

                is NetworkResult.Error -> {
                    Text(
                        text = result.message!!,
                        fontSize = 32.sp,
                        style = TextStyle(color = colorResource(id = R.color.primary))
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaceList(
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
            model = place.images.firstOrNull() ?: R.drawable.auth_image,
            contentDescription = place.name,
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(5.dp)),
            contentScale = ContentScale.Crop
        ) {
            it.load(place.images.firstOrNull() ?: R.drawable.auth_image)
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
        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.un_star),
            contentDescription = "like",
            modifier = Modifier.padding(end = 12.dp)
        )
    }
}

@Preview
@Composable
private fun PlaceListPreview() {
    PlaceList(places = MainViewModel().getListOfPlace(), onPlaceSelected = {})
}


@Preview
@Composable
private fun SearchScreenPreview() {
    /*SearchScreen(searchingText = "",
        onSearchingCancel = {},
        onSearchingTextChange = {},
        choosingFilter = Filter.DEFAULT,
        onFilterChange = {})*/
}