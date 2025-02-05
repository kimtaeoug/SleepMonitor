plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
    id("kotlinx-serialization")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    implementation(libs.kotlin.serialization.json)

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("com.google.truth:truth:1.1.3")
}
