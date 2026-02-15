package ua.deromeo.planty.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ua.deromeo.planty.data.local.entity.DiseaseEntity

@Dao
interface DiseaseDao {
    @Query("SELECT * FROM diseases WHERE plantId = :plantId")
    fun getByPlantId(plantId: Long): Flow<List<DiseaseEntity>>

    @Query("SELECT * FROM diseases WHERE id = :id")
    fun getById(id: Long): Flow<DiseaseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(diseases: List<DiseaseEntity>)
}
