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
            }
        }
    }
}

dependencies {
    implementation("me.gosimple:nbvcxz:1.5.0")
}

konforkLibPlugin.buildJs.set(false)
konforkLibPlugin.buildJvm.set(true)
