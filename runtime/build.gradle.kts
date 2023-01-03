plugins {
    java
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

group = "github.dqw4w9wgxcq.bot"
version = Versions.project

repositories {
    mavenCentral()
    maven("https://repo.runelite.net")
}

dependencies {
    runtimeOnly(project(":loader"))
    runtimeOnly(project(":injector"))
    runtimeOnly("org.ow2.asm:asm:${Versions.asm}")
    runtimeOnly("org.ow2.asm:asm-tree:${Versions.asm}")
    runtimeOnly("org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}")
    //runelite launcher uses a minified runelite-api dep, so some classes are missing
    runtimeOnly("net.runelite:runelite-api:${Versions.runelite}")
    runtimeOnly(group = "org.jboss.aerogear", name = "aerogear-otp-java", version = "1.0.0")
}

tasks {
    shadowJar {
        doLast {
            copy {
                from(shadowJar)
                into("${System.getProperty("user.home")}${File.separator}Desktop")
            }
        }
    }
}
