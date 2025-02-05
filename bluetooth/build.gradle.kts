plugins {
    id("module.android")
}

dependencies {
    implementation(projects.component)
    implementation(projects.model)
    implementation(projects.common)
    implementation(projects.design)
    implementation(projects.notification)
    implementation(libs.androidx.core)

    implementation("no.nordicsemi.android:ble-ktx:2.5.1")
    implementation("no.nordicsemi.android:ble-common:2.5.1")
    implementation("no.nordicsemi.android.support.v18:scanner:1.6.0")
}
