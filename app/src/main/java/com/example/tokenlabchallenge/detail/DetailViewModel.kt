package com.example.tokenlabchallenge.detail

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tokenlabchallenge.movie.MovieViewModel
import com.example.tokenlabchallenge.network.DetailProperty
import com.example.tokenlabchallenge.network.MovieApi
import com.example.tokenlabchallenge.network.MovieProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DetailViewModel (movieProperty: MovieProperty, app: Application) : AndroidViewModel(app) {

    private val _status = MutableLiveData<MovieViewModel.MovieApiStatus>()
    val status: LiveData<MovieViewModel.MovieApiStatus>
        get() = _status

    private val _detailProperty = MutableLiveData<DetailProperty>()
    val detailProperty: LiveData<DetailProperty>
        get() = _detailProperty

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main )

    init {
        getMovieDetails(movieProperty.id)
    }

    private fun getMovieDetails(idMovie: Int) {
        coroutineScope.launch {
            // Get the Deferred object for our Retrofit request
            val getPropertiesDeferred = MovieApi.retrofitService.getMovieDetailByIdAsync(idMovie)
            try {
                _status.value = MovieViewModel.MovieApiStatus.LOADING
                // Await the completion of our Retrofit request
                val result = getPropertiesDeferred.await()
                _detailProperty.value = result
                _status.value = MovieViewModel.MovieApiStatus.DONE
            } catch (e: Exception) {
                _status.value = MovieViewModel.MovieApiStatus.ERROR
            }
        }
    }
}
