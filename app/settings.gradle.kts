rootProject.name = "usermgt"

// unable to find a proper replacement for an jxr plugin

pluginManagement {
    plugins {
        id("org.siouan.frontend-jdk11") version "8.0.0"
        id("org.zeroturnaround.gradle.jrebel") version "1.1.12"
        id("org.gretty") version "4.1.0"
        kotlin("jvm") version "1.7.20"
        // "disabled", not included in gradle build yet (mostly because build fails).
        pmd
        id("com.github.spotbugs") version "6.0.0-beta.3"
        id("ca.cutterslade.analyze") version "1.9.1"
    }
}