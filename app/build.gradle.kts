import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    id("com.google.dagger.hilt.android")
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
}
configurations.all {
    resolutionStrategy {
        force("org.jetbrains:annotations:23.0.0")
    }
}


android {
    namespace = "ua.deromeo.planty"
    compileSdk = 35

    defaultConfig {
        applicationId = "ua.deromeo.planty"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }



        //load the values from .properties file
        val keystoreFile = project.rootProject.file("apikeys.properties")
        val properties = Properties()
        properties.load(keystoreFile.inputStream())

        //return empty key in case something goes wrong
        val apiWeatherKey = properties.getProperty("WEATHER_API_KEY") ?: ""
        val mapsKey = properties.getProperty("GOOGLE_MAPS_KEY") ?: ""
        buildConfigField(
            type = "String",
            name = "WEATHER_API_KEY",
            value = "\"${apiWeatherKey}\""
        )
        manifestPlaceholders["GOOGLE_MAPS_KEY"] = mapsKey

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.room.runtime.android)
    implementation(libs.litert.support.api)
    implementation(libs.litert)
    implementation(libs.firebase.common.ktx)
    implementation(libs.firebase.database.ktx)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.com.google.devtools.ksp.gradle.plugin)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    //coil
    implementation (libs.coil.compose)
    implementation (libs.accompanist.coil)

    // viewModel
    implementation (libs.androidx.lifecycle.viewmodel.compose)

    //navigation
    implementation(libs.androidx.navigation.compose)

    implementation(libs.play.services.location)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)

    //Room
    implementation(libs.room)
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
    ksp(libs.room.compiler)

    // Hilt
    implementation(libs.hilt.android)
    ksp (libs.dagger.compiler)
    ksp (libs.hilt.compiler)
    // Hilt ViewModel
    implementation(libs.androidx.hilt.navigation.compose)

    //CameraX
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.core)

    //Google Accompanist
    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.tensorflow.lite.select.tf.ops)
    implementation (libs.maps.compose)
    implementation (libs.play.services.maps)
    implementation(libs.ui.graphics)

    implementation (libs.capturable)
    implementation (libs.autosizetext.compose)

}
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}
