import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version Versions.kotlin
}

group = "github.dqw4w9wgxcq.bot"
version = Versions.project

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.runelite.net")
    }
}

apply<MavenPublishPlugin>()

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}")

    compileOnly("net.runelite:client:${Versions.runelite}")
    compileOnly(project(":loader"))
    compileOnly(group = "org.jboss.aerogear", name = "aerogear-otp-java", version = "1.0.0")
    compileOnly(group = "org.jetbrains", name = "annotations", version = "23.1.0")//version in kotlin-stdlib is 13.0
    compileOnly("org.projectlombok:lombok:${Versions.rlLombok}")
    annotationProcessor("org.projectlombok:lombok:${Versions.rlLombok}")
}

tasks {
    java {
        withSourcesJar()
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    withType<KotlinCompile> {
        kotlinOptions.freeCompilerArgs += listOf(
            "-Xuse-k2",
            "-Xjvm-default=all-compatibility"//for wrappers
        )
    }
}

configure<PublishingExtension> {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
        }
    }
}
