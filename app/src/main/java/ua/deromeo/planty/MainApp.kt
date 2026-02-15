package ua.deromeo.planty

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import ua.deromeo.planty.domain.repository.PlantRepository
import javax.inject.Inject

@HiltAndroidApp
class MainApp : Application() {
    @Inject
    lateinit var plantRepository: PlantRepository
}
