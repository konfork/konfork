import com.palantir.gradle.gitversion.VersionDetails

plugins {
    id("com.palantir.git-version")
    id("io.github.gradle-nexus.publish-plugin") version "1.0.0"
}

val versionDetails: groovy.lang.Closure<VersionDetails> by extra
val details = versionDetails()

group = "io.github.konfork"
version = cleanVersion(details)

task("versionDetails") {
    val details = versionDetails()
    println(details)
    println(project.version)
}

fun cleanVersion(details: VersionDetails): String =
    if (details.version.first() == 'v')
        details.version.drop(1)
    else
        details.version

//nexusPublishing {
//    repositories {
//        sonatype {
//            username.set(System.getenv("OSSRH_USER") ?: return@sonatype)
//            password.set(System.getenv("OSSRH_PASSWORD") ?: return@sonatype)
//        }
//    }
//}
