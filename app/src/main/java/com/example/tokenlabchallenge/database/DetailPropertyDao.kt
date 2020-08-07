package com.example.tokenlabchallenge.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DetailPropertyDao {

    @Insert
    fun insert(detailProperty: DetailProperty)

    @Query("select * from detail_property_table where :key = id")
    fun getDetailById(key: Int): DetailProperty?

}