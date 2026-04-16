pluginManagement {
    repositories {
        google()               // ✅ REQUIRED (fixes your error)
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "spacecolony"
include(":app")