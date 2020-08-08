package com.example.tokenlabchallenge.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tokenlabchallenge.database.DetailPropertyDao
import com.example.tokenlabchallenge.movie.MovieViewModel
import com.example.tokenlabchallenge.network.MovieApi
import kotlinx.coroutines.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import com.example.tokenlabchallenge.database.DetailProperty as DetailRoom

class DetailViewModel (private val detailDao: DetailPropertyDao, moviePropertyId: Int, app: Application) : AndroidViewModel(app) {

    private val _status = MutableLiveData<MovieViewModel.MovieApiStatus>()
    val status: LiveData<MovieViewModel.MovieApiStatus>
        get() = _status
    private val _statusConnection = MutableLiveData<MovieViewModel.MovieApiStatus>()
    val statusConnection: LiveData<MovieViewModel.MovieApiStatus>
        get() = _statusConnection

    private val _detailProperty = MutableLiveData<DetailRoom>()
    val detailProperty: LiveData<DetailRoom>
        get() = _detailProperty
    private val _countries = MutableLiveData<String>()
    val countries: LiveData<String>
        get() = _countries
    private val _releaseDate = MutableLiveData<String>()
    val releaseDate: LiveData<String>
        get() = _releaseDate
    private val _count = MutableLiveData<String>()
    val count: LiveData<String>
        get() = _count
    private val _voteAverage = MutableLiveData<String>()
    val voteAverage: LiveData<String>
        get() = _voteAverage

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main )

    init {
        _statusConnection.value = MovieViewModel.MovieApiStatus.LOADING
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
                            vote_count = result.vote_count
                        )
                        detailDao.insert(currentDetail)
                    }
                    // Set current values
                    _detailProperty.postValue(currentDetail)
                    _releaseDate.postValue(simpleDateFromString(currentDetail.release_date.toString()))
                    _voteAverage.postValue(currentDetail.vote_average.toString())
                    _count.postValue(currentDetail.vote_count.toString())
                    _countries.postValue(countriesList.toString()
                        .replace("[", "")
                        .replace("]", "")
                    )

                    // Set status
                    _status.postValue(MovieViewModel.MovieApiStatus.DONE)
                    _statusConnection.postValue(MovieViewModel.MovieApiStatus.DONE)
                } catch (e: Exception) {
                    try {
                        val localDetail = detailDao.getDetailById(idMovie)
                        _status.postValue(MovieViewModel.MovieApiStatus.LOADING)

                        // Set current values
                        _detailProperty.postValue(localDetail)
                        _voteAverage.postValue(localDetail?.vote_average.toString())
                        _releaseDate.postValue(simpleDateFromString(localDetail?.release_date.toString()))
                        _count.postValue(localDetail?.vote_count.toString())
                        _countries.postValue(localDetail?.production_countries.toString()
                            .replace("[", "")
                            .replace("]", "")
                        )

                        // Set status
                        _status.postValue(MovieViewModel.MovieApiStatus.DONE)
                        _statusConnection.postValue(MovieViewModel.MovieApiStatus.DONE)
                    } catch (e: Exception) {
                        _statusConnection.postValue(MovieViewModel.MovieApiStatus.ERROR)
                    }
                }
            }
        }
    }

    private fun simpleDateFromString(dateAsString: String): String {
        val originalFormat: DateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
        val targetFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
        val date: Date = originalFormat.parse(dateAsString)
        return targetFormat.format(date)
    }
}
