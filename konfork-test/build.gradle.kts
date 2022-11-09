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
                implementation(kotlin("test"))
            }
        }
    }
}

konforkLibPlugin.buildJs.set(true)
konforkLibPlugin.buildJvm.set(true)
