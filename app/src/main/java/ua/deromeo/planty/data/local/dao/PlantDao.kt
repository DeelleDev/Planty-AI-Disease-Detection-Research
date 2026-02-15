package ua.deromeo.planty.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ua.deromeo.planty.data.local.entity.PlantEntity

@Dao
interface PlantDao {
    @Query("SELECT * FROM plants")
    fun getAll(): Flow<List<PlantEntity>>

    @Query("SELECT * FROM plants WHERE id = :id")
    fun getById(id: Long): Flow<PlantEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plants: List<PlantEntity>)

    @Query("SELECT COUNT(*) FROM plants")
    suspend fun getCount(): Int
}