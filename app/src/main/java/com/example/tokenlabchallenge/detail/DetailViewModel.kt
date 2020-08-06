package com.example.tokenlabchallenge.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.tokenlabchallenge.network.MovieProperty


class DetailViewModel (movieProperty: MovieProperty, app: Application) : AndroidViewModel(app) {

    private val _selectedProperty = MutableLiveData<MovieProperty>()

    val selectedProperty: LiveData<MovieProperty>
        get() = _selectedProperty

    init {
        _selectedProperty.value = movieProperty
    }
}
