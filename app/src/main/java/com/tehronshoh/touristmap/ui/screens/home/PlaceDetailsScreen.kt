package com.tehronshoh.touristmap.ui.screens.home

import android.graphics.drawable.Drawable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
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
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.tehronshoh.touristmap.R
import com.tehronshoh.touristmap.model.Place
import com.tehronshoh.touristmap.remote.RetrofitClient

@Composable
fun PlaceDetailsScreen(place: Place, onNavigateBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        TopBar(place.isFavorite == 1, onNavigateBack)

        ScreenContent(place)
    }
}

@Composable
fun ScreenContent(place: Place) {
    val scrollState = rememberScrollState()
    Column(modifier = Modifier.fillMaxSize().verticalScroll(state = scrollState)) {
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

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ListOfImages(images: List<String>) {
    val listStateRemember = rememberLazyListState()

    LazyRow(state = listStateRemember) {
        items(images) { url ->
            if (images.first() == url)
                Spacer(modifier = Modifier.width(30.dp))

            Log.d("TAG_DETAILS", "ListOfImages: ${RetrofitClient.BASE_URL + url}")
            GlideImage(
                model = RetrofitClient.BASE_URL + url,
                contentDescription = "image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(
                    120.dp,
                    100.dp
                )
            ) {
                it.addListener(object : RequestListener<Drawable>{
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return true
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return true
                    }

                })
                    .placeholder(R.color.primary_100)
                    .error(R.color.black_10_alpha)
                    .load(RetrofitClient.BASE_URL + url)
            }
            Spacer(modifier = Modifier.width(20.dp))
        }
    }
    /*
        GlideLazyListPreloader(
            state = listStateRemember,
            data = images,
            size = Size(600f, 500f),
            numberOfItemsToPreload = 7,
            fixedVisibleItemCount = 2,
        ) { item, requestBuilder ->
            requestBuilder.load(RetrofitClient.BASE_URL + item)
        }*/
}

@Composable
private fun TopBar(isFav: Boolean, onNavigateBack: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(30.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
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
            contentDescription = "Back Button"
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
        onNavigateBack = {}
    )
}