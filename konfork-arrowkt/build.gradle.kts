plugins {
    id("konfork-lib")
}

repositories {
    mavenCentral()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":konfork-core"))
                implementation("io.arrow-kt:arrow-core:1.1.3")
            }
        }
    }
}

konforkLibPlugin.buildJs.set(false)
konforkLibPlugin.buildJvm.set(true)
