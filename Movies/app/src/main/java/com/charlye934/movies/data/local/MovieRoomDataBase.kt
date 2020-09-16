package com.charlye934.movies.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MovieEntity::class], version = 1, exportSchema = false)
abstract class MovieRoomDataBase : RoomDatabase(), MovieDao {
    abstract fun getMovieDato(): MovieDao
}