package com.tehronshoh.touristmap.ui.components

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.tehronshoh.touristmap.model.Place
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PlaceBottomSheet(
    place: Place,
    sheetState: ModalBottomSheetState,
    coroutineScope: CoroutineScope,
    buildRoute: () -> (Unit),
    onClose: () -> (Unit)
){
    Log.d("TAG_TEST", "PlaceBottomSheet: Opened!")
    ModalBottomSheetLayout(
        sheetState = sheetState,
        modifier = Modifier.fillMaxSize(),
        sheetContent = {
            Column (
                modifier = Modifier.fillMaxSize()
            ) {
              Text(
                  text = place.name
              )
                Button(
                    content = {
                        Text(
                            text = "Построить маршрут до \"${place.name}\""
                        )
                    },
                    onClick = {
                        buildRoute()
                    }
                )
            }
    }) {}

    BackHandler(sheetState.isVisible) {
        onClose()
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun PlaceBottomSheetPreview() {
    PlaceBottomSheet(
        Place("Парк Рудаки", 38.576290022103, 68.7847677535899, ""),
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Expanded),
        rememberCoroutineScope(),
        {}
    ) {}
}