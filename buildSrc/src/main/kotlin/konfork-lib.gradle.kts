plugins {
    kotlin("multiplatform")
    id("maven-publish")
    id("signing")
    id("com.palantir.git-version")
}

val gitVersion: groovy.lang.Closure<String> by extra
kotlin {
    group = "io.github.konfork"
    version = gitVersion()
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js(BOTH) {
        browser()
        nodejs()
    }
    sourceSets {
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

configure<PublishingExtension> {
    repositories {
        maven {
            name = "OSSRH"
            setUrl("https://oss.sonatype.org/service/local/staging/deploy/maven2")
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
