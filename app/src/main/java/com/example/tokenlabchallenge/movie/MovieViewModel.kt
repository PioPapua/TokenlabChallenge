package com.example.tokenlabchallenge.movie

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tokenlabchallenge.database.MoviePropertyDao
import com.example.tokenlabchallenge.database.MovieProperty as MovieRoom
import com.example.tokenlabchallenge.network.MovieApi
import kotlinx.coroutines.*

class MovieViewModel(private val movieDao: MoviePropertyDao, app: Application) : AndroidViewModel(app) {

    enum class MovieApiStatus { LOADING, ERROR, DONE }

    // The internal MutableLiveData String that stores the status of the most recent request
    private val _status = MutableLiveData<MovieApiStatus>()
    // The external immutable LiveData for the request status String
    val status: LiveData<MovieApiStatus>
        get() = _status

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
        getMoviesProperties()
    }

    private fun getMoviesProperties() {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                // Get the Deferred object for our Retrofit request
                val getPropertiesDeferred = MovieApi.retrofitService.getMoviesAsync()
                try {
                    _status.postValue(MovieApiStatus.LOADING)

                    // Await the completion of our Retrofit request
                    val listResult = getPropertiesDeferred.await()
                    // Create MovieProperty compatible with Room and add it to TokenlabChallengeDatabase if it doesn't exist.
                    for (item in listResult) {
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
                    _properties.postValue(movieDao.getAllMovies())
                    _status.postValue(MovieApiStatus.DONE)
                } catch (e: Exception) {
                    try {
                        _status.postValue(MovieApiStatus.LOADING)
                        _properties.postValue(movieDao.getAllMovies())
                    } catch (e: Exception) {
                        _status.postValue(MovieApiStatus.ERROR)
                    }
                }
            }
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
