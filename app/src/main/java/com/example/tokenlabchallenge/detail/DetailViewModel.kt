package com.example.tokenlabchallenge.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tokenlabchallenge.database.DetailPropertyDao
import com.example.tokenlabchallenge.database.DetailProperty as DetailRoom
import com.example.tokenlabchallenge.movie.MovieViewModel
import com.example.tokenlabchallenge.network.MovieApi
import kotlinx.coroutines.*

class DetailViewModel (private val detailDao: DetailPropertyDao, moviePropertyId: Int, app: Application) : AndroidViewModel(app) {

    private val _status = MutableLiveData<MovieViewModel.MovieApiStatus>()
    val status: LiveData<MovieViewModel.MovieApiStatus>
        get() = _status

    private val _detailProperty = MutableLiveData<DetailRoom>()
    val detailProperty: LiveData<DetailRoom>
        get() = _detailProperty

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main )

    init {
        getMovieDetails(moviePropertyId)
    }

    private fun getMovieDetails(idMovie: Int) {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    // Get the Deferred object for our Retrofit request
                    val getPropertiesDeferred =
                        MovieApi.retrofitService.getMovieDetailByIdAsync(idMovie)
                    _status.postValue(MovieViewModel.MovieApiStatus.LOADING)
                    // Await the completion of our Retrofit request

                    val result = getPropertiesDeferred.await() // Detail property from Retrofit
                    var currentDetail =
                        detailDao.getDetailById(result.id) // Detail property from Room

                    // Create and insert result in Room DB if it doesn't already exist.
                    val countriesList = mutableListOf<String>()
                    for (item in result.production_countries) {
                        countriesList.add(item.name)
                    }
                    if (currentDetail == null) {
                        currentDetail = DetailRoom(
                            id = result.id,
                            original_language = result.original_language,
                            overview = result.overview,
                            imgSrcUrl = result.imgSrcUrl,
                            production_countries = countriesList,
                            release_date = result.release_date,
                            title = result.title,
                            vote_average = result.vote_average,
                            count = result.count
                        )
                        detailDao.insert(currentDetail)
                    }
                    _detailProperty.postValue(currentDetail)
                    _status.postValue(MovieViewModel.MovieApiStatus.DONE)
                } catch (e: Exception) {
                    try {
                        _status.postValue(MovieViewModel.MovieApiStatus.LOADING)
                        _detailProperty.postValue(detailDao.getDetailById(idMovie))
                    } catch (e: Exception) {
                        _status.postValue(MovieViewModel.MovieApiStatus.ERROR)
                    }
                }
            }
        }
    }
}
