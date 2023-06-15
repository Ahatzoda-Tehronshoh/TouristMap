package com.tehronshoh.touristmap.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RouteSettings(
    var points: List<Place>,
    val routeType: RouteType,
    val transportType: TransportType,
    val drawingType: RouteDrawingType
) : Parcelable

enum class RouteType(val label: String) {
    ALL("Все пути"),
    SINGLE("Только один путь")
}

enum class RouteDrawingType(val label: String) {
    CLEAR("Создать новый м."),
    ADD("Добавить в м.")
}

enum class TransportType(val label: String) {
    WALKING("Пешком"),
    CAR("На автомобиле"),
    BICYCLE("На велосипеде")
}