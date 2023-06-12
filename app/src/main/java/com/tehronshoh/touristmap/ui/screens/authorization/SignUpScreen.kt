package com.tehronshoh.touristmap.ui.screens.authorization


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material3.Button
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
import androidx.compose.ui.res.colorResource
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
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.tehronshoh.touristmap.R
import com.tehronshoh.touristmap.extensions.isValidEmail
import com.tehronshoh.touristmap.model.Country
import com.tehronshoh.touristmap.model.User
import com.tehronshoh.touristmap.ui.components.CountryBottomSheetDialog
import com.tehronshoh.touristmap.ui.components.PasswordTextField
import com.tehronshoh.touristmap.ui.components.TopImage

const val TJK_NAME = "Republic of Tajikistan"
const val TJK_FLAG_URL = "https://flagcdn.com/w320/tj.png"

@Composable
fun SignUpScreen(
    fragmentManager: FragmentManager,
    isLoading: Boolean,
    onNavigateToLogIn: () -> Unit,
    onNavigateToMainWithoutLogIn: () -> Unit,
    onRegistrate: (User) -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        if (isLoading)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(2f), contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = colorResource(id = R.color.primary),
                    modifier = Modifier.align(alignment = Alignment.Center)
                )
            }

        SignUpContent(
            fragmentManager = fragmentManager,
            onNavigateToLogIn = onNavigateToLogIn,
            onNavigateToMainWithoutLogIn = onNavigateToMainWithoutLogIn,
            onRegistrate = onRegistrate
        )
    }
}

@Composable
private fun SignUpContent(
    fragmentManager: FragmentManager,
    onNavigateToLogIn: () -> Unit,
    onNavigateToMainWithoutLogIn: () -> Unit,
    onRegistrate: (User) -> Unit
) {
    val verticalScroll = rememberScrollState()

    var nickname by remember { mutableStateOf("") }
    var nicknameIsError by remember { mutableStateOf(false) }

    var login by remember { mutableStateOf("") }
    var loginIsError by remember { mutableStateOf(false) }
    var isValidEmail by remember { mutableStateOf(false) }

    var password by remember { mutableStateOf("") }
    var passwordIsError by remember { mutableStateOf(false) }

    var confirmPassword by remember { mutableStateOf("") }
    var confirmPasswordIsError by remember { mutableStateOf(false) }
    var passwordMatch by remember { mutableStateOf(false) }

    var country by remember { mutableStateOf(Country(TJK_NAME, TJK_FLAG_URL)) }

    var bottomSheetShowing by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        TopImage(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.18f)
                .align(Alignment.TopCenter)
        )

        Box(
            modifier = Modifier
                .zIndex(1f)
                .fillMaxWidth()
                .fillMaxHeight(0.87f)
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

                NickNameContainer(nickName = nickname, isError = nicknameIsError) {
                    nicknameIsError = false
                    nickname = it
                }

                EmailContainer(login, loginIsError) {
                    loginIsError = false
                    login = it
                    isValidEmail = it.isValidEmail()
                }

                PasswordContainer(password = password, passwordIsError = passwordIsError) {
                    passwordIsError = false
                    password = it
                }

                PasswordContainer(label = "Confirm password",
                    password = confirmPassword,
                    confirmPassword = password,
                    passwordIsError = confirmPasswordIsError,
                    onError = { passwordMatch = !it }) {
                    confirmPasswordIsError = false
                    confirmPassword = it
                }

                if (bottomSheetShowing)
                    CountryBottomSheetDialog(
                        fragmentManager = fragmentManager,
                        onCancel = {
                            bottomSheetShowing = false
                        }
                    ) {
                        bottomSheetShowing = false
                        country = it
                    }

                CountryChoose(country = country) {
                    bottomSheetShowing = true
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (nickname.isEmpty())
                            nicknameIsError = true
                        else if (!isValidEmail)
                            loginIsError = true
                        else if (password.length < 4)
                            passwordIsError = true
                        else if (!passwordMatch)
                            confirmPasswordIsError = true
                        else
                            onRegistrate(
                                User(
                                    nickName = nickname,
                                    login = login,
                                    password = password,
                                    country = "${country.officialName}#${country.pngUrl}"
                                )
                            )
                    },
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .fillMaxWidth(0.6f)
                ) {
                    Text("Регистрация")
                }

                ClickableContainer(
                    onNavigateToLogIn = onNavigateToLogIn,
                    onNavigateToMainWithoutLogIn = onNavigateToMainWithoutLogIn
                )
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun CountryChoose(
    country: Country,
    onClick: () -> Unit
) {

    Box(modifier = Modifier.fillMaxWidth().clickable {
            onClick()
        }) {

        Row(
            modifier = Modifier.fillMaxSize().clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = country.officialName + ":   ",
                fontFamily = FontFamily(Font(resId = R.font.montserrat_600)),
                fontSize = 12.sp,
                style = TextStyle(color = Color.Black, fontWeight = FontWeight.W500)
            )
            GlideImage(
                model = country.pngUrl,
                contentDescription = "Country flag",
                modifier = Modifier.size(30.dp)
            ) {
                it.load(country.pngUrl)
            }
            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.country_spinnen_icon),
                contentDescription = "choose country",
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun NickNameContainer(
    nickName: String,
    isError: Boolean,
    onNickNameChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Nickname",
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
            value = nickName,
            onValueChange = { onNickNameChange(it) },
            placeholder = {
                Text(
                    text = "tehron_shoh",
                    fontFamily = FontFamily(Font(resId = R.font.montserrat_600)),
                    fontSize = 12.sp,
                    style = TextStyle(color = Color.Black, fontWeight = FontWeight.W500),
                    modifier = Modifier.alpha(0.5f)
                )
            },
            isError = isError,
            visualTransformation = VisualTransformation.None,
            leadingIcon = {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.nick_name_icon),
                    contentDescription = "email",
                    tint = colorResource(id = R.color.primary),
                    modifier = Modifier.padding(start = 8.dp)
                )
            },
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
private fun PasswordContainer(
    label: String = "Password",
    password: String,
    confirmPassword: String = password,
    passwordIsError: Boolean,
    onError: (Boolean) -> Unit = {},
    onPasswordChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontFamily = FontFamily(Font(resId = R.font.montserrat_600)),
            fontSize = 12.sp,
            style = TextStyle(color = Color.Black, fontWeight = FontWeight.W400),
            modifier = Modifier
                .padding(top = 25.dp, start = 10.dp)
                .fillMaxWidth()
                .alpha(0.5f)
        )
        Spacer(modifier = Modifier.height(12.dp))
        PasswordTextField(
            text = password,
            confirmText = confirmPassword,
            onTextChanged = { text -> onPasswordChange(text) },
            hasError = passwordIsError,
            onError = onError,
            placeholder = {
                Text(
                    text = "•••••••••",
                    fontFamily = FontFamily(Font(resId = R.font.montserrat_600)),
                    fontSize = 12.sp,
                    style = TextStyle(color = Color.Black, fontWeight = FontWeight.W500),
                    modifier = Modifier.alpha(0.5f)
                )
            })
        Spacer(modifier = Modifier.height(8.dp))
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
            text = "Регистрация",
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
private fun ClickableContainer(
    onNavigateToLogIn: () -> Unit,
    onNavigateToMainWithoutLogIn: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        ClickableText(text = buildAnnotatedString {
            append("У вас есть аккаунт? ")
            withStyle(
                style = SpanStyle(
                    color = colorResource(id = R.color.primary),
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append("Вход")
            }
        }, onClick = { onNavigateToLogIn() })
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
        }, onClick = { onNavigateToMainWithoutLogIn() })
    }
}

@Preview
@Composable
private fun SignUpScreenPreview() {
    val previewFragmentManager = object : FragmentManager() {}
    SignUpScreen(
        fragmentManager = previewFragmentManager,
        onNavigateToLogIn = {},
        onNavigateToMainWithoutLogIn = {},
        isLoading = false
    ) {}
}