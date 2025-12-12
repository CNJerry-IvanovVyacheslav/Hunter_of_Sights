plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    // Подключаем Hilt
    alias(libs.plugins.hiltAndroid)
    id("kotlin-kapt")
}

android {
    namespace = "com.melongamesinc.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 28

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(project(":core:model"))

    // GPS Client
    implementation(libs.play.services.location) // Убедись, что добавил эту библиотеку в libs.versions.toml

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hiltCompiler)

    // Coroutines
    implementation(libs.androidx.core.ktx)
}