plugins {
    java
}

group = "github.dqw4w9wgxcq.bot"
version = Versions.project

repositories {
    mavenCentral()
    maven("https://repo.runelite.net")
}

apply<MavenPublishPlugin>()

dependencies {
    compileOnly("net.runelite:client:${Versions.runelite}")
    compileOnly("org.projectlombok:lombok:${Versions.rlLombok}")
    annotationProcessor("org.projectlombok:lombok:${Versions.rlLombok}")
}

tasks {
    java {
        withSourcesJar()
        //if used in a plugin, need 1.8.  j9 module-info breaks guava jar loader used by runelite
        sourceCompatibility = JavaVersion.VERSION_1_8
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
