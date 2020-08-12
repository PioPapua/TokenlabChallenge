package com.example.tokenlabchallenge.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface DetailPropertyDao {

    @Insert
    fun insert(detailProperty: DetailProperty)

    @Update
    fun update(detailProperty: DetailProperty)

    @Query("select * from detail_property_table where :key = id")
    fun getDetailById(key: Int): DetailProperty?

}