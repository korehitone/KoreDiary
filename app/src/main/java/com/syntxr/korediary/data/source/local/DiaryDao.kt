package com.syntxr.korediary.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryDao {

    @Insert
    suspend fun insert(postEntity: PostEntity)

    @Query("SELECT * FROM post")
    fun getStoredData() : Flow<List<PostEntity>>

    @Query("DELETE FROM post")
    suspend fun clear()

    @Query("DELETE FROM post WHERE uuid = :uuid")
    suspend fun delete(uuid: String)
}