plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
//    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    id("com.google.relay")
    id("com.google.gms.google-services")
    id("androidx.room")
    id("kotlin-parcelize")
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
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
        viewBinding = true
//        compose = false
        buildConfig = true
        //noinspection DataBindingWithoutKapt
        dataBinding = true
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

    viewBinding {
        enable = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    room {
        schemaDirectory("$projectDir/schemas")
    }

    sourceSets {
        getByName("main") {
            assets.srcDirs("src/main/assets")
        }
    }

    packaging {
        resources {
            excludes += "META-INF/DEPENDENCIES"
        }
    }
}

dependencies {
    // Core Android libraries
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.cardview)

    // Material Design
    implementation(libs.material)

    // Navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Google Play Services
    implementation(libs.google.play.services.auth)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.database)
    implementation(libs.firebase.common.ktx)
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.firebase.functions.ktx)

    // Room (Database)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.rxjava3)
    implementation(libs.androidx.room.guava)
    implementation(libs.androidx.room.paging)
    ksp(libs.androidx.room.compiler)
    testImplementation(libs.androidx.room.testing)

    // Dependency Injection (Hilt)
    implementation(libs.hilt.android.core)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

    // Kotlin
    implementation(kotlin("stdlib-jdk8"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // JavaX
    implementation(libs.javax.inject)

    // Desugaring
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // Other libraries
    implementation(libs.places)
    implementation(libs.mpandroidchart)
    implementation(libs.gson)
    implementation(libs.timber)
    implementation(libs.symbol.processing.api)

    // Testing
    implementation(libs.socketio.client)
    implementation(libs.androidx.localbroadcastmanager)

    testImplementation(libs.junit)
    testImplementation(libs.androidx.core.testing)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Metadata
    implementation(libs.kotlin.metadata.jvm)

    // Gmail
    implementation(libs.google.api.client)
    implementation(libs.google.api.client.android)
    implementation(libs.google.oauth.client.jetty)
    implementation(libs.google.api.services.gmail)
    implementation(libs.javax.mail)
    implementation(libs.slf4j.api)
    implementation(libs.slf4j.simple)
    implementation(libs.google.auth.library.oauth2.http)
    implementation(libs.google.play.services.auth)

    // androidx.lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
}