package com.tehronshoh.touristmap.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.tehronshoh.touristmap.R

@Composable
fun AuthorizationScreen(
    modifier: Modifier = Modifier,
    fontSize: Float = 32.0f,
    onNavigateToLogIn: () -> Unit,
    onNavigateToRegistration: () -> Unit,
    onNavigateToMain: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.blue_200)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(0.6f).zIndex(2f).padding(bottom = 48.dp),
        ) {
            AuthorizationButton(fontSize, stringResource(id = R.string.log_in)) {
                onNavigateToLogIn()
            }
            Divider(color = Color.Transparent, modifier = Modifier.height(16.dp))
            AuthorizationButton(fontSize, stringResource(id = R.string.sign_up)) {
                onNavigateToRegistration()
            }
        }
        Image(
            painter = painterResource(id = R.drawable.mountains),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .align(alignment = Alignment.BottomCenter)
        )
        FloatingActionButton(
            onClick = { onNavigateToMain() },
            modifier = Modifier
                .align(alignment = Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp),
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Text(
                text = "Войти\nкак гость",
                color = Color.DarkGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp)
            )
        }
    }
}

@Composable
private fun AuthorizationButton(fontSize: Float, text: String, onClick: () -> (Unit)) {
    Button(
        onClick = {
            onClick()
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            /*style = TextStyle(
                fontSize = with(LocalDensity.current) {
                    (fontSize * 1.0f).toSp()
                }
            ),*/
            modifier = Modifier.padding(vertical = 6.dp)
        )
    }
}

@Preview
@Composable
private fun AuthorizationScreenPreview() {
    AuthorizationScreen(onNavigateToLogIn = {}, onNavigateToRegistration = {}, onNavigateToMain = {})
}