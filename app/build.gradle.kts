plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.room)
}

android {
    namespace = "com.kotlity.myreminder"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.kotlity.myreminder"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            merges += "META-INF/LICENSE.md"
            merges += "META-INF/LICENSE-notice.md"
        }
    }
    room {
        schemaDirectory("$projectDir/schemas")
    }
}

dependencies {
    implementation(project(":core:resources"))
    implementation(project(":core:data"))
    implementation(project(":core:domain"))
    implementation(project(":core:presentation"))
    implementation(project(":core:alarm:data"))
    implementation(project(":core:alarm:domain"))
    implementation(project(":core:notification:data"))
    implementation(project(":core:notification:domain"))
    implementation(project(":feature_reminders:domain"))
    implementation(project(":feature_reminders:data"))
    implementation(project(":feature_reminders:presentation"))

    implementation(libs.bundles.android.core)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)

    implementation(libs.bundles.coroutines)

    implementation(libs.bundles.koin)

    implementation(libs.bundles.room)
    ksp(libs.androidx.room.compiler)

    implementation(libs.bundles.navigation)

    implementation(libs.threeTenabp)

    implementation(libs.cloudy)

    debugImplementation(libs.bundles.compose.debug)

    testImplementation(libs.bundles.test)

    androidTestImplementation(libs.bundles.android.test)
    androidTestImplementation(platform(libs.androidx.compose.bom))
}