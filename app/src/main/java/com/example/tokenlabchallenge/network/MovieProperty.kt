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
    val genres: List<String>
    //val release_date: Date
) : Parcelable

@Parcelize
class DetailProperty(
    val adult: Boolean,
    @Json(name = "backdrop_url") val backdropSrcUrl: String,
    val belongs_to_collection: MovieCollection,
    val budget: Int,
    val genres: List<String>,
    @Json(name = "homepage") val homepageUrl: String,
    val id: Int,
    val imdb_id: String,
    val original_language: String,
    val original_title: String,
    val overview: String,
    val popularity: Double,
    @Json(name = "poster_url") val imgSrcUrl: String,
    val production_companies: List<ProductionCompany>,
    val production_countries: List<ProductionCountry>,
    val release_date: Date,
    val revenue: Int,
    val runtime: Int,
    val spoken_languages: List<Language>,
    val status: String,
    val taglilne: String,
    val title: String,
    val video: Boolean,
    val vote_average: Double,
    val count: Int
) : Parcelable

@Parcelize
data class MovieCollection(
    val id: Int,
    val name: String,
    @Json(name = "poster_url") val imgSrcUrl: String,
    @Json(name = "backdrop_url") val backdropSrcUrl: String
) : Parcelable

@Parcelize
data class ProductionCompany(
    val id: Int,
    val name: String,
    @Json(name = "logo_url") val logoUrl: String,
    val origin_country: String
) : Parcelable

@Parcelize
data class ProductionCountry(
    @Json(name = "iso_3166_1") val countryCode: String,
    val name: String
) : Parcelable

@Parcelize
data class Language(
    @Json(name = "iso_639_1") val languageCode: String,
    val name: String
) : Parcelable