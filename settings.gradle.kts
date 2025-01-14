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
include(":core:testing")
include(":core:alarm:domain")
include(":core:alarm:data")
include(":core:notification:domain")
include(":core:notification:data")
include(":feature_reminders:domain")
include(":feature_reminders:data")
include(":feature_reminders:presentation")
include(":feature_reminder_editor:domain")
include(":feature_reminder_editor:data")
include(":feature_reminder_editor:presentation")
