package com.tehronshoh.touristmap.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tehronshoh.touristmap.model.Filter
import com.tehronshoh.touristmap.model.NetworkResult
import com.tehronshoh.touristmap.model.Place
import com.tehronshoh.touristmap.remote.RemoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    var listOfPlace = mutableStateOf<List<Place>>(listOf())

    private val _listOfPlaceLiveData = MutableLiveData<NetworkResult<List<Place>>>()
    val listOfPlaceLiveData: LiveData<NetworkResult<List<Place>>>
        get() = _listOfPlaceLiveData

    init {
        viewModelScope.launch {
            try {
                val response = RemoteRepository.getListOfPlaces(Filter.DEFAULT, -1)

                if (response.isSuccessful && response.body() != null)
                    listOfPlace.value = response.body()!!
            } catch (_: Exception) {
            }
        }
    }

    fun getListOfPlace(filter: Filter, userId: Int) = viewModelScope.launch(Dispatchers.IO) {
        try {
            _listOfPlaceLiveData.postValue(NetworkResult.Loading())
            val response = RemoteRepository.getListOfPlaces(Filter.DEFAULT, userId)

            if (response.isSuccessful && response.body() != null) {
                Log.d("TAG_PLACES", "getListOfPlace: ${response.body()!!}")
                _listOfPlaceLiveData.postValue(NetworkResult.Success(response.body()!!))
            } else
                _listOfPlaceLiveData.postValue(NetworkResult.Error(response.message()))
        } catch (e: Exception) {
            _listOfPlaceLiveData.postValue(NetworkResult.Error(e.message ?: "Try again!"))
        }
    }
}