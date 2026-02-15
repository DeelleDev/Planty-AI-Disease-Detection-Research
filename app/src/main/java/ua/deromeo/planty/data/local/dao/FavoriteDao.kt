package ua.deromeo.planty.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ua.deromeo.planty.data.local.entity.FavoriteEntity

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites WHERE id = :id AND type = :type")
    suspend fun getFavoriteById(id: Long, type: String): FavoriteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavorites(favorite: FavoriteEntity)

    @Delete
    suspend fun removeFromFavorites(favorite: FavoriteEntity)

    @Query("SELECT * FROM favorites")
    suspend fun getAllOnce(): List<FavoriteEntity>

}