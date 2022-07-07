import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version Versions.kotlin
}

group = "github.dqw4w9wgxcq.botapi"
version = Versions.project

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.runelite.net")
    }
}

apply<MavenPublishPlugin>()

dependencies {
    compileOnly("net.runelite:client:${Versions.runelite}")
    compileOnly(project(":loader-api"))
    implementation(group = "org.jboss.aerogear", name = "aerogear-otp-java", version = "1.0.0")
    compileOnly(group = "org.jetbrains", name = "annotations", version = "23.0.0")
    compileOnly("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")
}

tasks {
    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            javaParameters = true//so anon kotlin classes((asdf)->asdf) have metadata (tostring)
            freeCompilerArgs = listOf(
                "-Xjvm-default=all-compatibility",//so kotlin interfaces work with lombok delegate java classes(rlwrappers)
            )
        }
    }
}

configure<PublishingExtension> {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
        }
    }
}
