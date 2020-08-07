package com.example.tokenlabchallenge.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity (tableName = "movie_property_table")
data class MovieProperty constructor(
    @PrimaryKey
    val id: Int,
    val vote_average: Double,
    val title: String,
    val imgSrcUrl: String,
    val release_date: Date,
    val genres: List<String>
)