package com.tehronshoh.touristmap.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.tehronshoh.touristmap.R
import com.tehronshoh.touristmap.model.User
import com.tehronshoh.touristmap.ui.components.PasswordTextField

@Composable
fun SignInScreen(
    isLoading: Boolean,
    onLogIn: (User) -> Unit
) {
    var login by remember { mutableStateOf("") }

    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.blue_200))
    ) {
        if (isLoading)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(2f), contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.align(alignment = Alignment.Center))
            }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(id = R.color.blue_200)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = login,
                onValueChange = {
                    login = it
                },
                label = { Text("Login(email) or NickName") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                visualTransformation = VisualTransformation.None,
                modifier = Modifier.fillMaxWidth(0.7f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            PasswordTextField(text = password,
                confirmText = password,
                onTextChanged = { text -> password = text },
                label = { Text("Password") })
            Spacer(modifier = Modifier.height(8.dp))
            ClickableText(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = Color.Blue, textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append("Забыли пароль?")
                    }
                },
                onClick = {},
                modifier = Modifier.fillMaxWidth(0.7f),
                style = TextStyle(textAlign = TextAlign.End)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    onLogIn(
                        User(
                            nickName = "",
                            login = login,
                            password = password,
                            country = ""
                        )
                    )
                },
                enabled = (password.length >= 4),
                modifier = Modifier.fillMaxWidth(0.5f)
            ) {
                Text("Вход")
            }
        }
    }
}

@Preview
@Composable
private fun SignInPreview() {
    com.tehronshoh.touristmap.ui.screens.SignInScreen(true) {}
}