plugins {
    kotlin("multiplatform")
    id("maven-publish")
    id("signing")
    id("java-library")
    id("org.jetbrains.dokka")
}

project.group = rootProject.group
project.version = rootProject.version

val pluginSettings = extensions.create<KonforkLibPluginSettings>("konforkLibPlugin")
pluginSettings.buildJs.convention(true)
pluginSettings.buildJvm.convention(true)

kotlin {
    if (pluginSettings.buildJvm.get()) {
        jvm {
            compilations.all {
                kotlinOptions.jvmTarget = "1.8"
            }
            withJava()
            testRuns["test"].executionTask.configure {
                useJUnitPlatform()
            }
        }
    }
    if (pluginSettings.buildJs.get()) {
        js(BOTH) {
            browser()
            nodejs()
        }
    }
    sourceSets {
        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(project(":konfork-test"))
            }
        }
    }
}

tasks {
    val versionValidation by registering {
        doLast {
            println(project)
            val matches = project.version.toString().matches(Regex("^\\d+\\.\\d+\\.\\d+$"))
            if (!matches) throw IllegalArgumentException("Not a valid version: ${project.version}")
        }
    }
    publish {
        dependsOn(versionValidation)
    }
}

val dokkaHtml by tasks.getting(org.jetbrains.dokka.gradle.DokkaTask::class)

val javadocJar: TaskProvider<Jar> by tasks.registering(Jar::class) {
    dependsOn(dokkaHtml)
    archiveClassifier.set("javadoc")
    from(dokkaHtml.outputDirectory)
}

configure<PublishingExtension> {
    publications.withType<MavenPublication> {
        artifact(javadocJar)

        pom {
            val projectGitUrl = "https://github.com/konfork/konfork"
            name.set(rootProject.name)
            description.set("Declarative validations for Kotlin")
            url.set(projectGitUrl)
            inceptionYear.set("2022")
            licenses {
                license {
                    name.set("MIT")
                    url.set("https://opensource.org/licenses/MIT")
                }
            }
            developers {
                developer {
                    name.set("Geert Mulders")
                }
            }
            issueManagement {
                system.set("GitHub")
                url.set("$projectGitUrl/issues")
            }
            scm {
                connection.set("scm:git:$projectGitUrl")
                developerConnection.set("scm:git:$projectGitUrl")
                url.set(projectGitUrl)
            }
        }
    }
    repositories {
        maven {
            name = "OSSRH"
            setUrl("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2")
            credentials {
                username = System.getenv("OSSRH_USER") ?: return@credentials// throw IllegalArgumentException("No username on environment")
                password = System.getenv("OSSRH_PASSWORD") ?: return@credentials// throw IllegalArgumentException("No password on environment")
            }
        }
    }
}

configure<SigningExtension> {
    useGpgCmd()
    sign(publishing.publications)
}
