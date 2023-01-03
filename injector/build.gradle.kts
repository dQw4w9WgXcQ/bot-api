import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version Versions.kotlin
}

group = "github.dqw4w9wgxcq.bot"
version = Versions.project

repositories {
    mavenCentral()
}

apply<MavenPublishPlugin>()

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}")
    compileOnly("org.ow2.asm:asm:${Versions.asm}")
    compileOnly("org.ow2.asm:asm-tree:${Versions.asm}")
    compileOnly("org.slf4j:slf4j-api:${Versions.rlSlf4j}")
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
        kotlinOptions {
            jvmTarget = "11"
        }
        kotlinOptions.freeCompilerArgs += listOf("-Xuse-k2")
    }
}

configure<PublishingExtension> {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
        }
    }
}
