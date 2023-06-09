package com.tehronshoh.touristmap.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentManager
import com.tehronshoh.touristmap.R
import com.tehronshoh.touristmap.extensions.isValidEmail
import com.tehronshoh.touristmap.ui.components.CountryBottomSheetDialog
import com.tehronshoh.touristmap.ui.components.CountryListItem
import com.tehronshoh.touristmap.ui.components.PasswordTextField
import com.tehronshoh.touristmap.model.Country

const val TJK_NAME = "Republic of Tajikistan"
const val TJK_FLAG_URL = "https://flagcdn.com/w320/tj.png"

@Composable
fun SignUpScreen(fragmentManager: FragmentManager) {
    var nickname by remember { mutableStateOf("") }

    var login by remember { mutableStateOf("") }
    var isValidEmail by remember { mutableStateOf(false) }

    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordMatch by remember { mutableStateOf(false) }

    var country by remember { mutableStateOf(Country(TJK_NAME, TJK_FLAG_URL)) }

    var bottomSheetShowing by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.blue_200)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = nickname,
            onValueChange = { nickname = it },
            label = { Text("NickName") },
            modifier = Modifier.fillMaxWidth(0.7f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = login,
            onValueChange = {
                login = it
                isValidEmail = it.isValidEmail()
            },
            isError = !isValidEmail && login.isNotEmpty(),
            label = { Text("Login(email)") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            visualTransformation = VisualTransformation.None,
            modifier = Modifier.fillMaxWidth(0.7f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        PasswordTextField(text = password,
            confirmText = password,
            onTextChanged = { password = it },
            label = { Text("Password") })
        Spacer(modifier = Modifier.height(8.dp))
        PasswordTextField(text = confirmPassword,
            confirmText = password,
            onTextChanged = { confirmPassword = it },
            onError = { passwordMatch = (!it && (password.length > 4)) },
            label = { Text("Confirm Password") })
        if (bottomSheetShowing) CountryBottomSheetDialog(fragmentManager = fragmentManager,
            onCancel = {
                bottomSheetShowing = false
            }) {
            bottomSheetShowing = false
            country = it
        }
        Box(modifier = Modifier
            .fillMaxWidth(0.7f)
            .clickable {
                bottomSheetShowing = true
            }) {
            CountryListItem(country = country, onCountrySelected = {
                bottomSheetShowing = true
            })
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {

            },
            enabled = isValidEmail && passwordMatch && nickname.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text("Регистрировать")
        }
    }
}

@Preview
@Composable
private fun SignUpScreenPreview() {
    //SignUpScreen()
}