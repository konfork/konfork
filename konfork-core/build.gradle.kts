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
                implementation(project(":konfork-predicates"))
            }
        }
    }
}

konforkLibPlugin.buildJs.set(true)
konforkLibPlugin.buildJvm.set(true)
