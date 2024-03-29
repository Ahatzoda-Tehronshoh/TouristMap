package com.tehronshoh.touristmap.ui.components

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.tehronshoh.touristmap.model.Place
import com.tehronshoh.touristmap.model.RouteSettings
import com.tehronshoh.touristmap.ui.screens.home.PlaceDetailsScreen

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PlaceBottomSheet(
    place: Place,
    sheetState: ModalBottomSheetState,
    buildRoute: (RouteSettings) -> Unit,
    onClose: () -> Unit
){
    Log.d("TAG_TEST", "PlaceBottomSheet: Opened!")
    ModalBottomSheetLayout(
        sheetState = sheetState,
        modifier = Modifier.fillMaxSize(),
        sheetContent = {
           PlaceDetailsScreen(place = place, isBackButtonShow = false, showOnMap = { _, routeSettings ->
               if(routeSettings != null) {
                   buildRoute(routeSettings)
               }
           }) {
               onClose()
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
        Place(0, "Парк Рудаки", 38.576290022103, 68.7847677535899, "","", 1, listOf()),
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Expanded),
        {}
    ) {}
}