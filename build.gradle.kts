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
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}")

    compileOnly("net.runelite:client:${Versions.runelite}")
    compileOnly(project(":loader"))
    compileOnly(group = "org.jboss.aerogear", name = "aerogear-otp-java", version = "1.0.0")
    compileOnly(group = "org.jetbrains", name = "annotations", version = "23.0.0")
    compileOnly("org.projectlombok:lombok:1.18.20")
    annotationProcessor("org.projectlombok:lombok:1.18.20")
}

tasks {
    java {
        withSourcesJar()
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
        }
        kotlinOptions.freeCompilerArgs += listOf("-Xjvm-default=all-compatibility", "-Xuse-k2", "-java-parameters")
    }
}

configure<PublishingExtension> {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
        }
    }
}
