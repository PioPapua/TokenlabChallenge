package com.example.tokenlabchallenge.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tokenlabchallenge.database.DetailPropertyDao
import com.example.tokenlabchallenge.movie.MovieViewModel
import com.example.tokenlabchallenge.network.DetailProperty as DetailAPI
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
        _status.value = MovieViewModel.MovieApiStatus.LOADING
        getMovieDetails(moviePropertyId)
    }

    private fun getMovieDetails(idMovie: Int) {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                var roomDetail = detailDao.getDetailById(idMovie)
                if (getDetailFromAPI(idMovie) == null && roomDetail == null){
                    _statusConnection.postValue(MovieViewModel.MovieApiStatus.ERROR)
                } else {
                    // Update Room values in case something has been changed when calling the API.
                    roomDetail = detailDao.getDetailById(idMovie)
                    // Set current values
                    _detailProperty.postValue(roomDetail)
                    _releaseDate.postValue(simpleDateFromString(roomDetail!!.release_date.toString()))
                    _voteAverage.postValue(roomDetail.vote_average.toString())
                    _count.postValue(roomDetail.vote_count.toString())
                    _countries.postValue(roomDetail.production_countries.toString()
                        .replace("[", "")
                        .replace("]", "")
                    )
                    // Set status
                    _status.postValue(MovieViewModel.MovieApiStatus.DONE)
                    _statusConnection.postValue(MovieViewModel.MovieApiStatus.DONE)
                }
            }
        }
    }

    private suspend fun getDetailFromAPI(idMovie: Int): DetailAPI? {
        try {
            val detail = MovieApi.retrofitService.getMovieDetailByIdAsync(idMovie).await()
            val countriesList = mutableListOf<String>()
            for (item in detail.production_countries) {
                countriesList.add(item.name)
            }
            var currentDetail = detailDao.getDetailById(detail.id)
            if (currentDetail == null) {
                currentDetail = DetailRoom(
                    id = detail.id,
                    original_language = detail.original_language,
                    overview = detail.overview,
                    imgSrcUrl = detail.imgSrcUrl,
                    production_countries = countriesList,
                    release_date = detail.release_date,
                    title = detail.title,
                    vote_average = detail.vote_average,
                    vote_count = detail.vote_count
                )
                detailDao.insert(currentDetail)
            }
            return detail
        } catch (e: Exception){
            e.printStackTrace()
            return null
        }
    }

    private fun simpleDateFromString(dateAsString: String): String {
        val originalFormat: DateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
        val targetFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
        val date: Date = originalFormat.parse(dateAsString)
        return targetFormat.format(date)
    }
}
