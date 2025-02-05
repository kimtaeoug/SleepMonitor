plugins {
    id("module.android")
}

dependencies {
    implementation(projects.common)
    implementation(projects.design)
    implementation(projects.notification)

    implementation(libs.androidx.hilt.work)
    implementation(libs.androidx.work)
}
