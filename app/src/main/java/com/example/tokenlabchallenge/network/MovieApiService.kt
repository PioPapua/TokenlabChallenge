package com.example.tokenlabchallenge.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.text.SimpleDateFormat
import java.util.*

private const val BASE_URL = "https://desafio-mobile.nyc3.digitaloceanspaces.com/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .add(CustomDateAdapter())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

interface MovieApiService {

    @GET("movies")
    fun getMoviesAsync():
            Deferred<List<MovieProperty>>

    @GET("movies/{id}")
    fun getMovieDetailByIdAsync(@Path("id") id: Int):
            Deferred<DetailProperty>
}

// Public object to expose the Retrofit service to the rest of the app.
object MovieApi {
    val retrofitService : MovieApiService by lazy { retrofit.create(MovieApiService::class.java) }
}

class CustomDateAdapter : JsonAdapter<Date>() {
    private val dateFormat = SimpleDateFormat(SERVER_FORMAT, Locale.getDefault())
    @FromJson
    override fun fromJson(reader: JsonReader): Date? {
        return try {
            val dateAsString = reader.nextString()
            dateFormat.parse(dateAsString)
        } catch (e: Exception) {
            null
        }
    }
    @ToJson
    override fun toJson(writer: JsonWriter, value: Date?) {
        if (value != null) {
            writer.value(value.toString())
        }
    }
    companion object {
        const val SERVER_FORMAT = ("yyyy-MM-dd")
    }
}