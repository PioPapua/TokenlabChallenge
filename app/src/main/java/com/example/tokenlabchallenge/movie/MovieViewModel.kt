package com.example.tokenlabchallenge.movie

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tokenlabchallenge.network.MovieApi
import com.example.tokenlabchallenge.network.MovieProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MovieViewModel : ViewModel() {

    enum class MovieApiStatus { LOADING, ERROR, DONE }

    // The internal MutableLiveData String that stores the status of the most recent request
    private val _status = MutableLiveData<MovieApiStatus>()
    // The external immutable LiveData for the request status String
    val status: LiveData<MovieApiStatus>
        get() = _status

    private val _properties = MutableLiveData<List<MovieProperty>>()
    val properties: LiveData<List<MovieProperty>>
        get() = _properties

    private val _navigateToSelectedProperty = MutableLiveData<MovieProperty>()

    val navigateToSelectedProperty: LiveData<MovieProperty>
        get() = _navigateToSelectedProperty

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main )

    // Call getMoviesProperties() on initialize to be able to display status immediately.
    init {
        getMoviesProperties()
    }

    // Sets the value of _response to the MovieAPI status.
    private fun getMoviesProperties() {
        coroutineScope.launch {
            // Get the Deferred object for our Retrofit request
            val getPropertiesDeferred = MovieApi.retrofitService.getMovies()
            try {
                _status.value = MovieApiStatus.LOADING

                // Await the completion of our Retrofit request
                val listResult = getPropertiesDeferred.await()
                _properties.value = listResult
                _status.value = MovieApiStatus.DONE
            } catch (e: Exception) {
                _status.value = MovieApiStatus.ERROR
                _properties.value = ArrayList()
            }
        }
    }

    fun displayPropertyDetails(movieProperty: MovieProperty) {
        _navigateToSelectedProperty.value = movieProperty
    }

    fun displayPropertyDetailsComplete() {
        _navigateToSelectedProperty.value = null
    }

    // Once the viewModel finishes its job, we cancel the coroutine to announce Retrofit to stop.
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
