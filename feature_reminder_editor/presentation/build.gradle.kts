plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "com.kotlity.feature_reminder_editor"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "com.kotlity.InstrumentationTestRunner"
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
            merges += "META-INF/LICENSE.md"
            merges += "META-INF/LICENSE-notice.md"
        }
    }
}

dependencies {
    implementation(project(":feature_reminder_editor:domain"))
    implementation(project(":core:domain"))
    implementation(project(":core:alarm:domain"))
    implementation(project(":core:presentation"))
    implementation(project(":core:resources"))

    implementation(libs.bundles.android.core)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)

    implementation(libs.bundles.koin)

    implementation(libs.bundles.navigation)

    implementation(libs.threeTenabp)

    debugImplementation(libs.bundles.compose.debug)

    androidTestImplementation(project(":core:testing"))

    testImplementation(libs.bundles.test)
    testImplementation(libs.threeTenabp)

    androidTestImplementation(libs.bundles.android.test)
}