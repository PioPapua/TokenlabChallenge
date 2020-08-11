package com.example.tokenlabchallenge.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MoviePropertyDao {

    @Insert
    fun insert(movieProperty: MovieProperty)

    @Query("select * from movie_property_table")
    fun getAllMovies(): List<MovieProperty>?

    @Query("select * from movie_property_table where :key = id")
    fun getMovieById(key: Int): MovieProperty?

}