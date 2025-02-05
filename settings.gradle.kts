pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
//        jcenter()
    }
}
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "SleepMonitor"
include(":app")
include(":ui")
include(":data-resource")
include(":component")
include(":common")
include(":device")
include(":design")
include(":model")
include(":bluetooth")
include(":domain")
include(":data")
include(":local")
include(":notification")
include(":worker")
include(":simplerecyclerview")
include(":calendarview")


include(":remote")
include(":todo_design")
