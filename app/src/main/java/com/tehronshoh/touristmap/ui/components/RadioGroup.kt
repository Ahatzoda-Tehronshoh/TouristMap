package com.tehronshoh.touristmap.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tehronshoh.touristmap.R

@Composable
fun RadioGroup(selectedOption: String, options: List<String>, onOptionSelected: (String) -> Unit) {
    Row {
        options.forEach { option ->
            Row(
                Modifier.selectable(
                    selected = (option == selectedOption),
                    onClick = { onOptionSelected(option) }
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val selected = (option == selectedOption)

                val radioButtonColor =
                    if (selected) colorResource(id = R.color.primary) else colorResource(
                        id = R.color.black
                    )

                val radioButtonModifier =
                    if (selected) Modifier.height(20.dp) else Modifier
                        .height(20.dp)
                        .alpha(0.2f)

                RadioButton(
                    selected = selected,
                    onClick = null,
                    colors = RadioButtonDefaults.colors(selectedColor = radioButtonColor),
                    modifier = radioButtonModifier
                )

                val textColor =
                    if (selected) colorResource(id = R.color.primary) else colorResource(
                        id = R.color.black
                    )

                val textModifier = if (selected) Modifier.padding(end = 8.dp)
                else Modifier
                    .alpha(0.5f)
                    .padding(end = 8.dp)

                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = option,
                    fontSize = 10.sp,
                    fontFamily = FontFamily(Font(resId = R.font.montserrat_600)),
                    style = TextStyle(color = textColor, fontWeight = FontWeight.W500),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = textModifier
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}
