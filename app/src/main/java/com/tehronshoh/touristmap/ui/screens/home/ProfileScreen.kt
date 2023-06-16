package com.tehronshoh.touristmap.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.tehronshoh.touristmap.R
import com.tehronshoh.touristmap.model.NetworkResult
import com.tehronshoh.touristmap.model.TopPagerBarItem
import com.tehronshoh.touristmap.model.User
import com.tehronshoh.touristmap.ui.components.PlaceList
import com.tehronshoh.touristmap.ui.navigation.Screen
import com.tehronshoh.touristmap.ui.tool.LocalFilteredPlaces
import com.tehronshoh.touristmap.ui.tool.LocalUser
import kotlinx.coroutines.delay

object ProfileScreen {
    val pages = listOf(
        TopPagerBarItem(Screen.Favorite.route, Screen.Favorite.labelId!!),
        TopPagerBarItem(Screen.Friends.route, Screen.Friends.labelId!!)
    )
}

@Composable
fun ProfileScreen(
    profileOpenPage: TopPagerBarItem,
    onPageChange: (TopPagerBarItem) -> Unit,
    onNavigateToLogIn: () -> Unit,
    onNavigateToPlaceDetailsScreen: (Int) -> Unit
) {
    val user = LocalUser.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        TopContainer(user)

        Box(
            modifier = Modifier
                .zIndex(1f)
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .align(Alignment.BottomCenter)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape = RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp))
                    .background(color = Color.White)
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                if (user == null) {
                    val showingProgressBar = rememberSaveable {
                        mutableStateOf(true)
                    }

                    LaunchedEffect(Unit) {
                        delay(3000L)
                        showingProgressBar.value = false
                    }
                    if (showingProgressBar.value)
                        CircularProgressIndicator(
                            color = colorResource(id = R.color.primary)
                        )
                    else
                        Button(
                            onClick = {
                                onNavigateToLogIn()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(id = R.color.primary),
                                disabledContainerColor = colorResource(id = R.color.primary_100)
                            ),
                            modifier = Modifier
                                .padding(bottom = 12.dp)
                                .fillMaxWidth(0.7f)
                        ) {
                            Text("Войти")
                        }
                } else
                    Scaffold(
                        topBar = {
                            TopPagerBar(
                                pages = ProfileScreen.pages,
                                currentOpenPage = profileOpenPage
                            ) {
                                onPageChange(it)
                            }
                        },
                        modifier = Modifier.fillMaxSize(),
                        contentColor = Color.Yellow
                    ) { padding ->
                        Box(
                            modifier = Modifier
                                .padding(padding)
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            when (profileOpenPage.route) {
                                Screen.Favorite.route -> {
                                    when (val result = LocalFilteredPlaces.current) {
                                        is NetworkResult.Loading -> {
                                            CircularProgressIndicator(
                                                color = colorResource(id = R.color.primary),
                                                modifier = Modifier.padding(bottom = 40.dp)
                                            )
                                        }

                                        is NetworkResult.Success -> {
                                            val list = result.data!!.filter { (it.isFavorite == 1) }
                                            if (list.isEmpty())
                                                Image(
                                                    imageVector = ImageVector.vectorResource(id = R.drawable.not_favorites),
                                                    contentDescription = "you have not got favorites"
                                                )
                                            else
                                                PlaceList(
                                                    places = list,
                                                    onPlaceSelected = {
                                                        onNavigateToPlaceDetailsScreen(
                                                            it.id
                                                        )
                                                    }
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

                                Screen.Friends.route -> {
                                    Image(
                                        imageVector = ImageVector.vectorResource(id = R.drawable.not_friends),
                                        contentDescription = "you have not got favorites"
                                    )
                                }
                            }
                        }
                    }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun TopContainer(user: User?) {
    Box(
        modifier = Modifier
            .padding(bottom = 40.dp)
            .fillMaxWidth()
            .fillMaxHeight(0.35f),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.profile_image),
            contentDescription = "profile",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        if (user != null)
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.tehron_shoh),
                    contentDescription = "tehron",
                    modifier = Modifier.clip(RoundedCornerShape(50.dp))
                )
                Text(
                    text = user.nickName,
                    fontFamily = FontFamily(Font(resId = R.font.montserrat_600)),
                    fontSize = 16.sp,
                    style = TextStyle(color = Color.White, fontWeight = FontWeight.W500)
                )
                Spacer(modifier = Modifier.padding(top = 4.dp))
                Text(
                    text = user.login,
                    fontFamily = FontFamily(Font(resId = R.font.montserrat_600)),
                    fontSize = 12.sp,
                    style = TextStyle(color = Color.White, fontWeight = FontWeight.W400)
                )
                Spacer(modifier = Modifier.padding(top = 8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val image = user.country.split("#").lastOrNull()
                    if (image != null) {
                        GlideImage(
                            model = image,
                            contentDescription = "country",
                            modifier = Modifier.size(20.dp)
                        ) {
                            it.load(image)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = user.country.split("#")[0],
                        fontFamily = FontFamily(Font(resId = R.font.montserrat_600)),
                        fontSize = 12.sp,
                        style = TextStyle(color = Color.White, fontWeight = FontWeight.W400)
                    )
                }
            }
    }
}


@Preview
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen(ProfileScreen.pages[0], {}, {}) {

    }
}