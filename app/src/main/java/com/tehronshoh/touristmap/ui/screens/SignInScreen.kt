package com.tehronshoh.touristmap.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.tehronshoh.touristmap.R
import com.tehronshoh.touristmap.extensions.isValidEmail
import com.tehronshoh.touristmap.model.User
import com.tehronshoh.touristmap.ui.components.AppIcon
import com.tehronshoh.touristmap.ui.components.PasswordTextField

@Composable
fun SignInScreen(
    isLoading: Boolean, onLogIn: (User) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        if (isLoading) Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(2f), contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = colorResource(id = R.color.primary),
                modifier = Modifier.align(alignment = Alignment.Center)
            )
        }

        LogInContent(onLogIn)
    }
}

@Composable
private fun LogInContent(
    onLogIn: (User) -> Unit
) {
    val verticalScroll = rememberScrollState()
    var login by remember { mutableStateOf("") }
    var loginIsError by remember { mutableStateOf(false) }
    var loginIsValidEmail by remember { mutableStateOf(false) }

    var password by remember { mutableStateOf("") }
    var passwordIsError by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        TopImage(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.35f)
                .align(Alignment.TopCenter)
        )

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
                    .verticalScroll(verticalScroll, enabled = true)
                    .padding(vertical = 12.dp, horizontal = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                ScreenName()

                EmailContainer(login, loginIsError) {
                    loginIsError = false
                    login = it
                    loginIsValidEmail = it.isValidEmail()
                }

                PasswordContainer(password, passwordIsError) {
                    passwordIsError = false
                    password = it
                }

                Button(
                    onClick = {
                        if (!loginIsValidEmail)
                            loginIsError = true
                        else if (password.length < 4)
                            passwordIsError = true
                        else
                            onLogIn(
                                User(
                                    nickName = "", login = login, password = password, country = ""
                                )
                            )
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

                ClickableContainer()
            }
        }
    }
}

@Composable
private fun ClickableContainer() {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        ClickableText(text = buildAnnotatedString {
            append("У вас нет аккаунта? ")
            withStyle(
                style = SpanStyle(
                    color = colorResource(id = R.color.primary),
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append("Регистрация")
            }
        }, onClick = {})
        Spacer(modifier = Modifier.height(8.dp))
        ClickableText(text = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = colorResource(id = R.color.primary),
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append("Войти как гость")
            }
        }, onClick = {})
    }
}

@Composable
private fun PasswordContainer(password: String, passwordIsError: Boolean, onPasswordChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Password",
            fontFamily = FontFamily(Font(resId = R.font.montserrat_600)),
            fontSize = 12.sp,
            style = TextStyle(color = Color.Black, fontWeight = FontWeight.W400),
            modifier = Modifier
                .padding(top = 25.dp, start = 10.dp)
                .fillMaxWidth()
                .alpha(0.5f)
        )
        Spacer(modifier = Modifier.height(12.dp))
        PasswordTextField(text = password,
            confirmText = password,
            onTextChanged = { text -> onPasswordChange(text) },
            hasError = passwordIsError,
            placeholder = {
                Text(
                    text = "•••••••••",
                    fontFamily = FontFamily(Font(resId = R.font.montserrat_600)),
                    fontSize = 12.sp,
                    style = TextStyle(color = Color.Black, fontWeight = FontWeight.W500),
                    modifier = Modifier.alpha(0.5f)
                )
            })
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun EmailContainer(email: String, isError: Boolean, onEmailChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Email",
            fontFamily = FontFamily(Font(resId = R.font.montserrat_600)),
            fontSize = 12.sp,
            style = TextStyle(color = Color.Black, fontWeight = FontWeight.W400),
            modifier = Modifier
                .padding(top = 16.dp, start = 10.dp)
                .fillMaxWidth()
                .alpha(0.5f)
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = email,
            onValueChange = { onEmailChange(it) },
            placeholder = {
                Text(
                    text = "tehron_shoh@example.com",
                    fontFamily = FontFamily(Font(resId = R.font.montserrat_600)),
                    fontSize = 12.sp,
                    style = TextStyle(color = Color.Black, fontWeight = FontWeight.W500),
                    modifier = Modifier.alpha(0.5f)
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            visualTransformation = VisualTransformation.None,
            leadingIcon = {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.email_icon),
                    contentDescription = "email",
                    tint = colorResource(id = R.color.primary),
                    modifier = Modifier.padding(start = 8.dp)
                )
            },
            isError = isError,
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            ),
            modifier = Modifier
                .clip(RoundedCornerShape(40.dp))
                .fillMaxWidth()
                .background(color = colorResource(id = R.color.primary_100))
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun ScreenName() {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Вход",
            fontFamily = FontFamily(Font(resId = R.font.montserrat_600)),
            fontSize = 28.sp,
            style = TextStyle(color = Color.Black, fontWeight = FontWeight.W500),
            modifier = Modifier.fillMaxWidth()
        )
        Divider(
            color = colorResource(id = R.color.primary),
            modifier = Modifier
                .padding(top = 4.dp)
                .width(40.dp)
                .height(2.dp)
                .align(Alignment.Start)
        )
    }
}

@Composable
private fun TopImage(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = R.drawable.auth_image),
            contentDescription = "Logo",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth()
        )
        AppIcon(modifier = Modifier.padding(bottom = 40.dp))
    }
}

@Preview
@Composable
private fun SignInPreview() {
    SignInScreen(true) {}
}