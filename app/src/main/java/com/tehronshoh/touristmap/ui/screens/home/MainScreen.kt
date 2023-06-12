package com.tehronshoh.touristmap.ui.screens.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tehronshoh.touristmap.R
import com.tehronshoh.touristmap.model.TopPagerBarItem
import com.tehronshoh.touristmap.ui.navigation.Screen

object MainScreen {
    val pages = listOf(
        TopPagerBarItem(Screen.Search.route, Screen.Search.labelId!!),
        TopPagerBarItem(Screen.Map.route, Screen.Map.labelId!!)
    )
}

@Composable
fun MainScreen() {
    var currentOpenPage by rememberSaveable {
        mutableStateOf(MainScreen.pages[0])
    }

    Scaffold(
        topBar = {
            TopPagerBar(
                currentOpenPage = currentOpenPage
            ) {
                currentOpenPage = it
            }
        },
        modifier = Modifier.fillMaxSize(),
        contentColor = Color.Yellow
    ) { padding ->
        val scaffoldPadding = padding

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(id = R.color.white)),
            contentAlignment = Alignment.Center
        ) {
            when(currentOpenPage.route) {
                Screen.Search.route -> {
                    SearchScreen()
                }
                Screen.Map.route -> {
                    MapScreen()
                }
            }
        }
    }
}

@Composable
fun TopPagerBar(currentOpenPage: TopPagerBarItem, onPageChange: (TopPagerBarItem) -> Unit) {
    BottomNavigation(
        backgroundColor = Color.Transparent,
        contentColor = Color.Transparent,
        elevation = 0.dp,
        modifier = Modifier.padding(start = 30.dp, end = 30.dp, top = 8.dp)
    ) {
        val currentRoute = currentOpenPage.route

        MainScreen.pages.forEach { page ->
            Log.d("TAG_MAIN", "TopPagerBar: ${page.route} - $currentRoute")
            val selected = (currentRoute == page.route)

            val labelColor = if (selected)
                colorResource(id = R.color.primary)
            else
                colorResource(id = R.color.inactive_color)

            val padding = if (page.route == Screen.Search.route)
                PaddingValues(end = 15.dp)
            else
                PaddingValues(start = 15.dp)

            val modifier = if (selected)
                Modifier
                    .padding(padding)
                    .clip(RoundedCornerShape(10.dp))
                    .border(
                        width = 1.5.dp,
                        color = colorResource(id = R.color.primary),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .background(color = colorResource(id = R.color.primary_100))
                    .height(40.dp)
            else
                Modifier
                    .padding(padding)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color = colorResource(id = R.color.primary_100))
                    .height(40.dp)


            BottomNavigationItem(
                selected = selected,
                onClick = {
                    onPageChange(page)
                },
                icon = {
                    Text(
                        text = stringResource(page.labelId),
                        fontFamily = FontFamily(Font(resId = R.font.montserrat_600)),
                        fontSize = 12.sp,
                        style = TextStyle(color = labelColor, fontWeight = FontWeight.W500)
                    )
                },
                modifier = modifier
            )
        }
    }
}

@Preview
@Composable
private fun MainScreenPreview() {
    MainScreen()
}