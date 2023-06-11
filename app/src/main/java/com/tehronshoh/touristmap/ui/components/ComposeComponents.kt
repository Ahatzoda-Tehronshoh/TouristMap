package com.tehronshoh.touristmap.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tehronshoh.touristmap.R


@Composable
fun PasswordTextField(
    text: String,
    confirmText: String,
    placeholder: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    semanticContentDescription: String = "",
    hasError: Boolean = false,
    onTextChanged: (text: String) -> Unit,
    onError: (Boolean) -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    val showPassword = remember { mutableStateOf(false) }
    val matchError = remember { mutableStateOf(false) }

    onError(matchError.value)

    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        TextField(
            value = text,
            onValueChange = onTextChanged,
            placeholder = placeholder,
            keyboardOptions = KeyboardOptions.Default.copy(
                autoCorrect = true, keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            singleLine = true,
            isError = hasError || matchError.value,
            visualTransformation = if (showPassword.value) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val (icon, iconColor) = if (showPassword.value) {
                    Pair(
                        Icons.Filled.Visibility, Color.DarkGray
                    )
                } else {
                    Pair(
                        Icons.Filled.VisibilityOff, Color.LightGray
                    )
                }

                IconButton(onClick = { showPassword.value = !showPassword.value }) {
                    Icon(
                        icon, contentDescription = "Visibility", tint = iconColor
                    )
                }
            },
            leadingIcon = {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.password_icon),
                    contentDescription = "password",
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
                disabledIndicatorColor = Color.Transparent,
            ),
            modifier = Modifier
                .clip(RoundedCornerShape(40.dp))
                .fillMaxWidth()
                .background(color = colorResource(id = R.color.primary_100))
                .semantics { contentDescription = semanticContentDescription }
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (confirmText != text) {
            Text(
                text = stringResource(id = R.string.error_password_no_match),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.semantics { contentDescription = "ConfirmPasswordMessage" },
            )
            matchError.value = true
        } else {
            matchError.value = false
        }
    }
}