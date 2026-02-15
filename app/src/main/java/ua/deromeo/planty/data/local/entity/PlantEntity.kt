package ua.deromeo.planty.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plants")
data class PlantEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val botanicalName: String,
    val scientificName: String,
    val alsoKnownAs: String,
    val description: String,
    val genus: String,
    val family: String,
    val order: String,
    val plantClass: String,
    val division: String,
    val temperature: String,
    val light: String,
    val hardinessZone: String,
    val growthRate: String,
    val soilType: String,
    val soilDrainage: String,
    val soilPH: String,
    val watering: String,
    val fertilizer: String,
    val pruning: String,
    val propagation: String,
    val humidity: String,
    val transplanting: String,
    val commonPestsAndDiseases: String,
    val features: String,
    val uses: String,
    val interestingFacts: String,
    val imageUrl: String,
    val imageUrl1: String,
    val imageUrl2: String,
    val imageUrl3: String
)