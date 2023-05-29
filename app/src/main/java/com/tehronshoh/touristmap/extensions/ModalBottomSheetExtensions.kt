package com.tehronshoh.touristmap.extensions

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
fun ModalBottomSheetState.showBottomSheetPlace(coroutineScope: CoroutineScope) = coroutineScope.launch { show() }

@OptIn(ExperimentalMaterialApi::class)
fun ModalBottomSheetState.hideBottomSheetPlace( coroutineScope: CoroutineScope) = coroutineScope.launch { hide() }