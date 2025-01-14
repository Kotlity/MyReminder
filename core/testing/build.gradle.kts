plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

android {
    namespace = "com.kotlity"
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
    room {
        schemaDirectory("$projectDir/schemas")
    }
    packaging {
        resources {
            merges += "META-INF/LICENSE.md"
            merges += "META-INF/LICENSE-notice.md"
        }
    }
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:alarm:domain"))
    implementation(project(":core:alarm:data"))

    implementation(libs.bundles.android.core)

    implementation(libs.bundles.coroutines)

    implementation(libs.bundles.koin)

    implementation(libs.bundles.room)
    ksp(libs.androidx.room.compiler)

    implementation(libs.bundles.test)
    implementation(libs.bundles.android.test)

    testImplementation(libs.bundles.test)

    androidTestImplementation(libs.bundles.android.test)

}