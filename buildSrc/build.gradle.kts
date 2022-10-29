group = "io.github."

plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.20")
    implementation("com.palantir.git-version:com.palantir.git-version.gradle.plugin:0.15.0")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.7.20")
}
