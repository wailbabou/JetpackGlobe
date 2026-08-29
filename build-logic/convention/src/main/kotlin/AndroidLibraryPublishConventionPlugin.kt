import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create

/**
 * Convention plugin that applies maven-publish for JitPack compatibility.
 * JitPack builds the library directly from source — no signing or OSSRH required.
 * POM metadata is read from ext properties set in the module's build.gradle.kts.
 */
class AndroidLibraryPublishConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("maven-publish")

            afterEvaluate {
                extensions.configure<PublishingExtension> {
                    publications {
                        create<MavenPublication>("release") {
                            groupId = project.findProperty("POM_GROUP_ID")?.toString()
                                ?: "com.ouail"
                            artifactId = project.findProperty("POM_ARTIFACT_ID")?.toString()
                                ?: project.name
                            version = project.findProperty("POM_VERSION")?.toString()
                                ?: "1.0.0"

                            // Attach the release AAR (available after project evaluation)
                            from(project.components.getByName("release"))

                            pom {
                                name.set(
                                    project.findProperty("POM_NAME")?.toString() ?: project.name
                                )
                                description.set(
                                    project.findProperty("POM_DESCRIPTION")?.toString()
                                        ?: "An interactive Jetpack Compose 3D Globe component"
                                )
                                url.set(
                                    project.findProperty("POM_URL")?.toString()
                                        ?: "https://github.com/wailbabou/JetpackGlobe"
                                )
                                licenses {
                                    license {
                                        name.set("MIT License")
                                        url.set("https://opensource.org/licenses/MIT")
                                    }
                                }
                                developers {
                                    developer {
                                        id.set("wailbabou")
                                        name.set("Ouail Bellal")
                                    }
                                }
                                scm {
                                    url.set("https://github.com/wailbabou/JetpackGlobe")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
