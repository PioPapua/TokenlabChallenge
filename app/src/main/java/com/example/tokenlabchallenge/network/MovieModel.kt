package com.example.tokenlabchallenge.network

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize
import java.util.*

// The property names of this data classes are used by Moshi to match the names of values in JSON.

@Parcelize
data class MovieProperty(
    val id: Int,
    val vote_average: Double,
    val title: String,
    @Json(name = "poster_url") val imgSrcUrl: String,
    val release_date: Date,
    val genres: List<String>
) : Parcelable

@Parcelize
class DetailProperty(
    val id: Int,
    val original_language: String,
    val overview: String,
    @Json(name = "poster_url") val imgSrcUrl: String,
    val production_countries: List<ProductionCountry>,
    val release_date: Date,
    val title: String,
    val vote_average: Float,
    val count: Int?
) : Parcelable

@Parcelize
data class ProductionCountry(
    @Json(name = "iso_3166_1") val countryCode: String,
    val name: String
) : Parcelable