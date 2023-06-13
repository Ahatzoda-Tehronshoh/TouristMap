package com.tehronshoh.touristmap.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tehronshoh.touristmap.R

@Composable
fun SearchScreen(
    searchingText: String,
    modifier: Modifier = Modifier,
    onSearchingTextChange: (String) -> Unit,
    onSearchingCancel: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.white))
            .padding(horizontal = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            if (searchingText.isNotEmpty()) Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.back_button),
                contentDescription = "filter",
                modifier = Modifier.padding(end = 20.dp).clickable {
                    onSearchingCancel()
                }
            )

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

            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.filter_icon),
                contentDescription = "filter",
                modifier = Modifier.padding(start = 20.dp)
            )
        }

        Box(modifier = Modifier.weight(1f))
    }
}

@Preview
@Composable
private fun SearchScreenPreview() {
    SearchScreen(searchingText = "", onSearchingCancel = {}, onSearchingTextChange = {})
}