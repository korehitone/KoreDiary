package com.syntxr.korediary.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PostEntity::class],
    version = 1,
    exportSchema = true
)
abstract class DiaryDatabase : RoomDatabase() {
    abstract val dao: DiaryDao

    companion object {
        const val DB_NAME = "diary.db"
    }
}