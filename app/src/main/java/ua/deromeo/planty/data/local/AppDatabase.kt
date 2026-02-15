package ua.deromeo.planty.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ua.deromeo.planty.data.local.dao.DiseaseDao
import ua.deromeo.planty.data.local.dao.FavoriteDao
import ua.deromeo.planty.data.local.dao.HistoryDao
import ua.deromeo.planty.data.local.dao.PlantDao
import ua.deromeo.planty.data.local.entity.DiseaseEntity
import ua.deromeo.planty.data.local.entity.FavoriteEntity
import ua.deromeo.planty.data.local.entity.HistoryEntity
import ua.deromeo.planty.data.local.entity.PlantEntity

@Database(
    entities = [PlantEntity::class, DiseaseEntity::class, FavoriteEntity::class, HistoryEntity::class],
    version = 13,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao
    abstract fun diseaseDao(): DiseaseDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun historyDao(): HistoryDao

}