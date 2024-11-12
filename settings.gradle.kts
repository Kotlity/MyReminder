pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MyReminder"
include(":app")
include(":core:presentation")
include(":core:resources")
include(":core:domain")
include(":core:data")
include(":core:alarm:domain")
include(":core:alarm:data")
include(":core:notification:domain")
include(":core:notification:data")
