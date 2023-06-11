package com.tehronshoh.touristmap.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.tehronshoh.touristmap.R

@Composable
fun AppIcon(
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 32.sp
) {
    Box(modifier = modifier
        .fillMaxWidth()
        .background(color = Color.Transparent)) {
        Text(
            text = stringResource(R.string.tajikguide),
            fontSize = fontSize,
            fontFamily = FontFamily(Font(resId = R.font.montserrat_600)),
            style = TextStyle(
                color = Color.White,
                fontWeight = FontWeight.W600
            ),
            modifier = Modifier.align(alignment = Alignment.Center)
        )
    }
}

@Preview
@Composable
private fun AppIconPreview() {
    AppIcon()
}