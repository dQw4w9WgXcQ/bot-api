plugins {
    java
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

group = "github.dqw4w9wgxcq.botapi"
version = Versions.project

repositories {
    maven {
        url = uri("https://repo.runelite.net")
    }
    mavenCentral()
}

dependencies {
    compileOnly("net.runelite:client:${Versions.runelite}")
    implementation(project(":loader-api"))
    compileOnly("org.projectlombok:lombok:1.18.20")
    annotationProcessor("org.projectlombok:lombok:1.18.20")
}

tasks {
    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    shadowJar {
        archiveFileName.set("static-loader.jar")

        doLast {
            copy {
                from(shadowJar)
                into("${System.getProperty("user.home")}${File.separator}Desktop")
            }
        }
    }
}