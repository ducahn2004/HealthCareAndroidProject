plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.relay") version "0.3.12"
}

android {
    namespace = "com.example.healthcareproject"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.healthcareproject"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    buildTypes {
        getByName("debug") {
            buildConfigField("String", "SOCKET_URL", "\"http://192.168.1.100:3000\"")
        }
        getByName("release") {
            buildConfigField("String", "SOCKET_URL", "\"https://your-production-server.com\"")
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
    sourceSets {
        getByName("main") {
            assets {
                srcDirs("src/main/assets")
            }
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.google.play.services.auth)
    implementation(libs.androidx.cardview)
    implementation(libs.mpandroidchart)
    implementation(libs.gson)
    implementation(libs.socketio.client)
    implementation(libs.androidx.localbroadcastmanager)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}