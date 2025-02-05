plugins {
    id("module.android")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs")
    id("kotlinx-serialization")

    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    buildFeatures {
        compose = true
        dataBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.2.0-beta03"
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(projects.common)
    implementation(projects.component)
    implementation(projects.domain)
    implementation(projects.design)
    implementation(projects.model)
    implementation(projects.dataResource)
    implementation(projects.worker)
    implementation(projects.notification)
    implementation(projects.calendarview)

    implementation(libs.androidx.core)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.runtime)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.androidx.constraintlayout)

    implementation(libs.glide)
    implementation(project(mapOf("path" to ":local")))
    kapt(libs.glide.compiler)

    implementation(libs.androidx.viewmodel)

    implementation(libs.ted.permission.coroutine)
    implementation(libs.ted.tedonactivityresult)

    implementation(libs.lottie)
    implementation(libs.androidx.work)
    implementation(projects.simplerecyclerview)

//    implementation("com.dino.library:simplerecyclerview:0.7.1")
    implementation("no.nordicsemi.android.support.v18:scanner:1.6.0")
    implementation("pl.bclogic:pulsator4droid:1.0.3")
    implementation(libs.kotlin.serialization.json)
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("io.github.ParkSangGwon:range-bar-chart:0.0.6")

    implementation("com.github.liuguangqiang.swipeback:library:1.0.2@aar")

//    implementation("com.haibin:calendarview:3.7.1")

    implementation("com.github.skydoves:balloon:1.4.7")
    implementation("com.google.android.exoplayer:exoplayer:2.18.1")

    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.9.0")

    implementation("androidx.compose.runtime:runtime:1.0.0")
    implementation("androidx.appcompat:appcompat:1.4.0")
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.compose.ui:ui:1.0.0")
    implementation("androidx.compose.material3:material3:1.0.1")
    implementation("androidx.compose.ui:ui-tooling-preview:1.0.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
    implementation("androidx.activity:activity-compose:1.3.1")

    implementation("androidx.compose.foundation:foundation:1.3.1")
    implementation ("com.orhanobut:logger:2.2.0")

}
