package com.example.videoclub.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.videoclub.data.local.model.FavoritesEntity
import com.example.videoclub.data.local.model.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Query("SELECT * FROM movies ORDER BY rowid ASC")
    fun observePopularMovies(): Flow<List<MovieEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMovies(movies: List<MovieEntity>)


    @Query("DELETE FROM movies")
    suspend fun clearMovies()

    @Query("SELECT * FROM movies WHERE id = :id")
    fun observeMovie(id: String): Flow<MovieEntity?>


    // Favorite
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setFavorite(favorite: FavoritesEntity)

    @Query("SELECT * FROM favorites")
    fun observeFavorites(): Flow<List<FavoritesEntity>>

    @Query(
        """
            SELECT isFavourite FROM favorites WHERE id = :movieId
        """
    )
    fun isFavorite(movieId: String): Flow<Boolean>
}