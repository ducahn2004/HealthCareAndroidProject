plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
//    alias(libs.plugins.compose.compiler) apply false
    id("com.google.relay") version "0.3.12" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    id("androidx.room") version "2.7.0" apply false
    id ("org.jetbrains.kotlin.jvm") version "2.1.10" apply false
}
