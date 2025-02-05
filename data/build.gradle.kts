plugins {
    id("module.kotlin")
}

dependencies {
    implementation(projects.domain)
    implementation(projects.dataResource)
    implementation(projects.model)
}
