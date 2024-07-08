plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.task1"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.task1"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val localProperties: java.util.Properties by rootProject.ext
        val mapsApiKey = localProperties.getProperty("MAPS_API_KEY") ?: "\"YOUR_DEFAULT_API_KEY\""
        buildConfigField("String", "MAPS_API_KEY", mapsApiKey)
        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey.removeSurrounding("\"")
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.legacy.support.v4)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // googlemap
    implementation("com.google.android.gms:play-services-maps:19.0.0")

    //Gson
    implementation("com.google.code.gson:gson:2.9.0")

    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.cardview:cardview:1.0.0")
}