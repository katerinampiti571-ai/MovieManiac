package com.example.videoclub.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.videoclub.data.local.model.FavoritesEntity
import com.example.videoclub.data.local.model.MovieEntity

@Database(
    entities = [
        MovieEntity::class,
        FavoritesEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}