package ua.deromeo.planty.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ua.deromeo.planty.data.local.entity.HistoryEntity

@Dao
interface HistoryDao {
    @Query("SELECT * FROM histories WHERE id = :id")
    fun getHistoryById(id: Long): Flow<HistoryEntity?>

    @Insert
    suspend fun insert(diagnosis: HistoryEntity): Long

    @Query("SELECT * FROM histories ORDER BY timestamp DESC")
    fun getAll(): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM histories ORDER BY timestamp DESC LIMIT 3")
    fun getLastThree(): Flow<List<HistoryEntity>>

    @Query("DELETE FROM histories")
    suspend fun clearAll()
}
