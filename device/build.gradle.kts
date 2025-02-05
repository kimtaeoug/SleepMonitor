plugins {
    id("module.android")
}

dependencies {
    implementation(projects.design)
    implementation(projects.component)
    implementation(projects.common)

    implementation(libs.androidx.core)
    implementation(libs.material)
    implementation("androidx.ads:ads-identifier:1.0.0-alpha04")
}
