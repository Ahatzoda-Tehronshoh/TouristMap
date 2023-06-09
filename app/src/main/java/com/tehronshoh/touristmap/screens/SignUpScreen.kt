package com.tehronshoh.touristmap.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentManager
import com.tehronshoh.touristmap.R
import com.tehronshoh.touristmap.components.CountryBottomSheetDialog
import com.tehronshoh.touristmap.components.CountryListItem
import com.tehronshoh.touristmap.components.PasswordTextField
import com.tehronshoh.touristmap.model.Country

const val TJK_NAME = "Republic of Tajikistan"
const val TJK_FLAG_URL = "https://flagcdn.com/w320/tj.png"

@Composable
fun SignUpScreen(fragmentManager: FragmentManager) {
    var nickname by remember { mutableStateOf("") }
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
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
        OutlinedTextField(value = nickname,
            onValueChange = { nickname = it },
            label = { Text("NickName") })
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = login, onValueChange = { login = it }, label = { Text("Login") })
        Spacer(modifier = Modifier.height(16.dp))
        PasswordTextField(text = password,
            confirmText = "",
            onTextChanged = { password = it },
            label = { Text("Password") })
        Spacer(modifier = Modifier.height(8.dp))
        PasswordTextField(text = confirmPassword,
            confirmText = password,
            onTextChanged = { confirmPassword = it },
            label = { Text("Confirm Password") })
        Spacer(modifier = Modifier.height(12.dp))
        if (bottomSheetShowing) CountryBottomSheetDialog(
            fragmentManager = fragmentManager,
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
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = {

        }, modifier = Modifier.fillMaxWidth(0.7f)) {
            Text("Регистрировать")
        }
    }
}

@Preview
@Composable
fun SignUpScreenPreview() {
    //SignUpScreen()
}