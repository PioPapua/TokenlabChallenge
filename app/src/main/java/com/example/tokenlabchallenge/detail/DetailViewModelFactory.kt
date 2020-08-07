package com.example.tokenlabchallenge.detail

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tokenlabchallenge.database.DetailPropertyDao

class DetailViewModelFactory(
    private val dataSource: DetailPropertyDao,
    private val moviePropertyId: Int,
    private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(dataSource, moviePropertyId, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
