import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

group = "teksturepako.pakku"
version = rootProject.version

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

application {
    mainClass.set("teksturepako.pakku.MainKt")
    applicationName = "Pakku"
    applicationDefaultJvmArgs = listOf("--enable-native-access=ALL-UNNAMED")
}

dependencies {
    implementation(project(":pakku-core"))
}

tasks.jar {
    archiveFileName.set("pakku.jar")
    manifest {
        attributes("Main-Class" to "teksturepako.pakku.MainKt")
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    // Fix "Invalid signature file"
    exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA")
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }
    })
}

distributions {
    main {
        contents {
            into("lib") {
                from(tasks.jar)
            }
        }
    }
}

tasks.withType<Tar>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<Zip>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
