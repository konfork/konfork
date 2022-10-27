plugins {
    kotlin("multiplatform")
    id("maven-publish")
    id("signing")
}

kotlin {
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
        mavenCentral()
    }
}

configure<SigningExtension> {
    useGpgCmd()
    sign(publishing.publications)
}
