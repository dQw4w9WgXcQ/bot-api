plugins {
    java
    id("com.github.johnrengelman.shadow") version "6.1.0"
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
    compileOnly("org.projectlombok:lombok:${Versions.rlLombok}")
    //runelite launcher uses a minified runelite-api dep, so some classes are missing
    runtimeOnly("net.runelite:runelite-api:${Versions.runelite}") {
        isTransitive = false
    }
    runtimeOnly("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}")
    runtimeOnly(group = "org.jboss.aerogear", name = "aerogear-otp-java", version = "1.0.0")
    annotationProcessor("org.projectlombok:lombok:${Versions.rlLombok}")
}

tasks {
    java {
        //if used in a plugin, need 1.8.  j9 module-info breaks guava jar loader
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_11
    }

    shadowJar {
        doLast {
            copy {
                from(shadowJar)
                into("${System.getProperty("user.home")}${File.separator}Desktop")
            }
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
