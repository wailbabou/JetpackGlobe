package jetpackglobe.dev.conventionplugin.buildlogic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension,
) {
    commonExtension.apply {
        buildFeatures.apply {
            compose = true
        }

        dependencies {
            val bom = libs.findLibrary("androidx-compose-bom").get()
            val compose = libs.findBundle("compose").get()

            "implementation"(platform(bom))
            "implementation"(compose)
            "androidTestImplementation"(platform(bom))
        }
    }
}
