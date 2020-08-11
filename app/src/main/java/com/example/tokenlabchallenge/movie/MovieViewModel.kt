package com.example.tokenlabchallenge.movie

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tokenlabchallenge.database.MoviePropertyDao
import com.example.tokenlabchallenge.database.MovieProperty as MovieRoom
import com.example.tokenlabchallenge.network.MovieApi
import com.example.tokenlabchallenge.network.MovieProperty as MovieAPI
import kotlinx.coroutines.*
import java.lang.Exception

class MovieViewModel(private val movieDao: MoviePropertyDao, app: Application) : AndroidViewModel(app) {

    enum class MovieApiStatus { LOADING, ERROR, DONE }

    // The internal MutableLiveData String that stores the status of the most recent request
    private val _status = MutableLiveData<MovieApiStatus>()
    // The external immutable LiveData for the request status String
    val status: LiveData<MovieApiStatus>
        get() = _status

    private val _statusConnection = MutableLiveData<MovieApiStatus>()
    val statusConnection: LiveData<MovieApiStatus>
        get() = _statusConnection

    private val _properties = MutableLiveData<List<MovieRoom>>()
    val properties: LiveData<List<MovieRoom>>
        get() = _properties

    private val _navigateToSelectedProperty = MutableLiveData<MovieRoom>()
    val navigateToSelectedProperty: LiveData<MovieRoom>
        get() = _navigateToSelectedProperty

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main )

    // Call getMoviesProperties() on initialize to be able to display status immediately.
    init {
        _statusConnection.value = MovieApiStatus.LOADING
        getMoviesProperties()
    }

    private fun getMoviesProperties() {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                if (getMoviesFromAPI().isNullOrEmpty() && movieDao.getAllMovies().isNullOrEmpty()){
                    _statusConnection.postValue(MovieApiStatus.ERROR)
                } else {
                    _properties.postValue (movieDao.getAllMovies())
                    _status.postValue(MovieApiStatus.DONE)
                    _statusConnection.postValue(MovieApiStatus.DONE)
                }
            }
        }
    }

    private suspend fun getMoviesFromAPI(): List<MovieAPI>?{
        try {
            // Await the completion of our Retrofit request, and get the Deferred object
            val movies =  MovieApi.retrofitService.getMoviesAsync().await()
            for (item in movies) {
                if (movieDao.getMovieById(item.id) == null) {
                    val movieRoom = MovieRoom(
                        id = item.id,
                        vote_average = item.vote_average,
                        title = item.title,
                        imgSrcUrl = item.imgSrcUrl,
                        release_date = item.release_date,
                        genres = item.genres
                    )
                    movieDao.insert(movieRoom)
                }
            }
            return movies
        } catch (e: Exception){
            e.printStackTrace()
            return listOf()
        }
    }

    fun displayPropertyDetails(movieProperty: MovieRoom) {
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
