plugins {
    `kotlin-dsl`
}

group = "jetpackglobe.dev.conventionplugin.buildlogic"

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.compiler.plugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "jetpackglobe.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidApplicationCompose") {
            id = "jetpackglobe.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        register("androidLibrary") {
            id = "jetpackglobe.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "jetpackglobe.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("androidLibraryPublish") {
            id = "jetpackglobe.android.library.publish"
            implementationClass = "AndroidLibraryPublishConventionPlugin"
        }
    }
}
