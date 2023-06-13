package com.tehronshoh.touristmap.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tehronshoh.touristmap.model.Filter
import com.tehronshoh.touristmap.model.NetworkResult
import com.tehronshoh.touristmap.model.Place
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _listOfPlaceLiveData = MutableLiveData<NetworkResult<List<Place>>>()
    val listOfPlaceLiveData: LiveData<NetworkResult<List<Place>>>
        get() = _listOfPlaceLiveData

    fun getListOfPlace(): List<Place> {
        return listOf(
            Place(0, "Парк Рудаки", 38.576290022103, 68.7847677535899, "", listOf()),
            Place(1, "Дворец Нации", 38.57625532932752, 68.77876043971129, "", listOf()),
            Place(
                2,
                "Парк национального флага",
                38.58121398293717,
                68.78074208988657,
                "",
                listOf()
            ),
            Place(
                3,
                "Национальный музей Таджикистана",
                38.582388153207894,
                68.77991596953152,
                "",
                listOf()
            ),
            Place(
                4,
                "Филиал МГУ им. М.В. Ломоносова",
                38.57935204500182,
                68.79011909252935,
                "",
                listOf()
            )
        )
    }

    fun getListOfPlace(filter: Filter) = viewModelScope.launch(Dispatchers.IO) {
        _listOfPlaceLiveData.postValue(NetworkResult.Loading())

        val listOfPlaces = listOf(
            Place(0, "Парк Рудаки", 38.576290022103, 68.7847677535899, "", listOf()),
            Place(1, "Дворец Нации", 38.57625532932752, 68.77876043971129, "", listOf()),
            Place(
                2,
                "Парк национального флага",
                38.58121398293717,
                68.78074208988657,
                "",
                listOf()
            ),
            Place(
                3,
                "Национальный музей Таджикистана",
                38.582388153207894,
                68.77991596953152,
                "",
                listOf()
            ),
            Place(
                4,
                "Филиал МГУ им. М.В. Ломоносова",
                38.57935204500182,
                68.79011909252935,
                "",
                listOf()
            )
        ).sortedWith(compareBy {
            when (filter) {
                Filter.BY_NAME -> it.name
                Filter.BY_DESTINATION -> it.latitude
                Filter.DEFAULT -> it.id
            }
        })

        _listOfPlaceLiveData.postValue(NetworkResult.Success(listOfPlaces))
    }
}