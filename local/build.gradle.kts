plugins {
    id("module.android")
}

dependencies {
    implementation(projects.data)
    implementation(projects.common)
    implementation(projects.model)

    implementation(libs.gson)
    implementation(libs.androidx.room)
    kapt(libs.androidx.room.compiler)

    testImplementation("junit:junit:4.13.2")
}
