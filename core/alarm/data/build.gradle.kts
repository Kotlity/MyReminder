plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.kotlity.core.alarm.data"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

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
}

dependencies {
    implementation(project(":core:alarm:domain"))
    implementation(project(":core:notification:domain"))
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:resources"))

    implementation(libs.bundles.android.core)

    implementation(libs.bundles.koin)

    implementation(libs.threeTenabp)

    testImplementation(project(":core:testing"))
    testImplementation(libs.bundles.test)
}