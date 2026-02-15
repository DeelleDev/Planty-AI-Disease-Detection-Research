package ua.deromeo.planty.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = true) val uniqueId: Long = 0, val id: Long, val type: String
)
