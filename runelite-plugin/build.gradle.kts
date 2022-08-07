plugins {
    java
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

tasks{
    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}