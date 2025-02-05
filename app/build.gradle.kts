plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    defaultConfig {
        applicationId = ApplicationId.id
        compileSdk = Config.compileSdk
        minSdk = Config.minSdk
        targetSdk = Config.targetSdk

        versionCode = Releases.versionCode
        versionName = Releases.versionName
        multiDexEnabled = true
    }

    buildTypes {
        named("debug") {
            isMinifyEnabled = true
            proguardFiles("proguard-rules.pro")
        }
        named("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.2.0-beta03"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    buildFeatures {
        compose = true
        dataBinding = true
    }
}

dependencies {
    implementation(projects.ui)
    implementation(projects.design)
    implementation(projects.component)
    implementation(projects.common)
    implementation(projects.domain)
    implementation(projects.data)
    implementation(projects.local)
    implementation(projects.device)
    implementation(projects.model)
    implementation(projects.bluetooth)
    implementation(projects.worker)
    implementation(projects.remote)

    implementation(libs.androidx.multidex)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.coroutines.android)
    implementation(libs.kotlin.serialization.json)
    implementation(libs.androidx.work)

    coreLibraryDesugaring(libs.desugarJdkLibs)

    implementation("androidx.compose.runtime:runtime:1.0.0")

}
