pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")
        maven ("https://repo.repsy.io/mvn/chrynan/public")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven ("https://repo.repsy.io/mvn/chrynan/public")
    }
}

rootProject.name = "Kore Diary"
include(":app")
