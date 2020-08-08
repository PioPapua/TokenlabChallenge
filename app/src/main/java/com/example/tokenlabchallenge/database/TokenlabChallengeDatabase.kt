package com.example.tokenlabchallenge.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    // Define every table on the Database
    entities = [MovieProperty::class, DetailProperty::class],
    version = 3,
    exportSchema = false)

@TypeConverters(Converters::class)

abstract class TokenlabChallengeDatabase : RoomDatabase() {

    abstract val movieDao: MoviePropertyDao
    abstract val detailDao: DetailPropertyDao

    companion object {

        @Volatile
        private var INSTANCE: TokenlabChallengeDatabase? = null

        fun getInstance(context: Context): TokenlabChallengeDatabase {
            synchronized(this) { // Only one thread of execution can enter the block at a time.
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        TokenlabChallengeDatabase::class.java,
                        "tokenlab_challenge_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}