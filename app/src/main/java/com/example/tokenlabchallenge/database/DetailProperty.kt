package com.example.tokenlabchallenge.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity (tableName = "detail_property_table")
data class DetailProperty constructor(
    @PrimaryKey
    val id: Int,
    val original_language: String,
    val overview: String,
    val imgSrcUrl: String,
    val production_countries: List<String>,
    val release_date: Date,
    val title: String,
    val vote_average: Float,
    val vote_count: Int
)