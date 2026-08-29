import com.android.build.api.dsl.LibraryExtension
import jetpackglobe.dev.conventionplugin.buildlogic.configureAndroidCompose
import jetpackglobe.dev.conventionplugin.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class AndroidLibraryComposeConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.run {
            pluginManager.apply(libs.findPlugin("kotlin-compose").get().get().pluginId)
            val extension = extensions.getByType<LibraryExtension>()
            configureAndroidCompose(extension)
        }
    }
}
