plugins {
    java
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
    compileOnly("net.runelite:client:${Versions.runelite}")
    compileOnly("org.projectlombok:lombok:1.18.20")
    annotationProcessor("org.projectlombok:lombok:1.18.20")
}

tasks {
    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

configure<PublishingExtension> {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
        }
    }
}
