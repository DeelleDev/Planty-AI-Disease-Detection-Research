package ua.deromeo.planty.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "diseases", foreignKeys = [ForeignKey(
        entity = PlantEntity::class,
        parentColumns = ["id"],
        childColumns = ["plantId"],
        onDelete = ForeignKey.CASCADE
    )], indices = [Index(value = ["plantId"])]
)
data class DiseaseEntity(
    @PrimaryKey val id: Long,
    val plantId: Long,
    val name: String,
    val fullName: String,
    val scientificName: String,
    val alsoKnownAs: String,
    val description: String,
    val symptoms: String,
    val treatment: String,
    val prevention: String,
    val imageUrl: String,
    val imageUrl1: String,
    val imageUrl2: String,
    val imageUrl3: String
)