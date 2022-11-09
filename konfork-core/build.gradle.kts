plugins {
    id("konfork-lib")
}

repositories {
    mavenCentral()
}

konforkLibPlugin.buildJs.set(true)
konforkLibPlugin.buildJvm.set(true)
