plugins {
    alias(libs.plugins.jetpackglobe.android.library)
    alias(libs.plugins.jetpackglobe.android.library.compose)
    alias(libs.plugins.jetpackglobe.android.library.publish)
}

android {
    namespace = "com.ouail.globe"

    // Expose the release variant for maven-publish
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

// POM metadata consumed by AndroidLibraryPublishConventionPlugin
ext["POM_GROUP_ID"] = "com.ouail"
ext["POM_ARTIFACT_ID"] = "globe"
ext["POM_VERSION"] = "1.0.0"
ext["POM_NAME"] = "JetpackGlobe"
ext["POM_DESCRIPTION"] = "An interactive Jetpack Compose 3D Globe component with Fibonacci sphere distribution, depth blur, and shortest-path centering."
ext["POM_URL"] = "https://github.com/wailbabou/JetpackGlobe"

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.ui.tooling.preview)
}
