package ua.deromeo.planty.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "histories")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val imagePath: String,
    val topPredictions: String,
    val latitude: Double?,
    val longitude: Double?
)
