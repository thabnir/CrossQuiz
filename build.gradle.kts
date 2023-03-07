
import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "com.thabnir"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}


kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("com.opencsv:opencsv:5.7.1")
                implementation("org.jetbrains.compose.ui:ui-graphics-desktop:1.1.0")
                implementation("org.jetbrains.compose.ui:ui-geometry-desktop:1.1.0")
                implementation("org.jetbrains.compose.ui:ui-unit-desktop:1.1.1")
                implementation("org.jetbrains.compose.ui:ui-util-desktop:1.1.1")
                implementation("org.jetbrains.compose.ui:ui-text-desktop:1.1.1")
                implementation("com.github.tkuenneth:nativeparameterstoreaccess:0.1.0")

            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Quizzer"
            packageVersion = "1.0.0"
        }
    }
}
