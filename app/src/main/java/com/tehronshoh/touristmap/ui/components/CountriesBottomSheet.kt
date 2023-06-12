package com.tehronshoh.touristmap.ui.components

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tehronshoh.touristmap.R
import com.tehronshoh.touristmap.model.Country
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URL

const val URL_ALL_COUNTRIES_NAME_AND_FLAGS = "https://restcountries.com/v3.1/all?fields=name,flags"

@Composable
fun CountryBottomSheetDialog(
    fragmentManager: FragmentManager,
    onCancel: () -> Unit,
    onCountrySelected: (Country) -> Unit
) {
    val context = LocalContext.current
    val countries by remember { mutableStateOf<MutableList<Country>>(mutableListOf()) }
    var searchQuery by remember { mutableStateOf(TextFieldValue()) }
    var isLoading by remember { mutableStateOf(true) }
    var bottomSheetState by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = Unit) {
        isLoading = true

        try {
            val json = withContext(Dispatchers.IO) {
                URL(URL_ALL_COUNTRIES_NAME_AND_FLAGS).readText()
            }
            val countriesArray = JSONArray(json)
            for (i in 0 until countriesArray.length()) {
                val country = countriesArray.getJSONObject(i)
                countries.add(
                    Country(
                        officialName = country.getJSONObject("name").getString("official"),
                        pngUrl = country.getJSONObject("flags").getString("png")
                    )
                )
            }
            Log.d("TAG_AUTH", "CountryBottomSheetDialog: $countries")
        } catch (e: Exception) {
            Log.d("TAG_AUTH", "CountryBottomSheetDialog: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    class ComposeBottomSheetDialogFragment : BottomSheetDialogFragment() {

        override fun getTheme(): Int = R.style.BottomSheetDialogTheme

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
            BottomSheetDialog(requireContext(), theme)


        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View = ComposeView(context).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                if (isLoading) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                            .fillMaxSize()
                    ) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                            .fillMaxSize()
                    ) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = { Text(text = "Search") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Box(modifier = Modifier.weight(1f)) {
                            CountryList(
                                countries = countries,
                                searchQuery = searchQuery,
                                onCountrySelected = {
                                    bottomSheetState = false
                                    onCountrySelected(it)
                                    dismiss()
                                }
                            )
                        }
                    }
                }
            }
        }

        override fun onCancel(dialog: DialogInterface) {
            onCancel()
            super.onCancel(dialog)
        }
    }

    val fragment = ComposeBottomSheetDialogFragment()
    if (bottomSheetState)
        fragment.show(fragmentManager, null)
    else
        fragment.dismiss()
}

@Composable
fun CountryList(
    countries: List<Country>,
    searchQuery: TextFieldValue,
    onCountrySelected: (Country) -> Unit
) {
    val filteredCountries = remember(searchQuery.text) {
        countries.filter { it.officialName.contains(searchQuery.text, ignoreCase = true) }
    }

    LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
        items(filteredCountries) { country ->
            CountryListItem(country = country, onCountrySelected = onCountrySelected)
            Divider(
                color = Color.LightGray,
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CountryListItem(country: Country, onCountrySelected: (Country) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.90f)
            .clickable { onCountrySelected(country) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = country.officialName,
            fontSize = 18.sp,
            fontFamily = FontFamily(Font(resId = R.font.montserrat_600)),
            style = TextStyle(color = Color.Black, fontWeight = FontWeight.W500),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(vertical = 8.dp)
                .align(Alignment.CenterStart)
        )
        GlideImage(
            model = country.pngUrl,
            contentDescription = "Country flag",
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.CenterEnd)
        ) {
            it.load(country.pngUrl)
        }
    }
}

@Preview
@Composable
fun CountryListItemPreview() {
    CountryListItem(country = Country("Tajikistan", ""), onCountrySelected = {})
}