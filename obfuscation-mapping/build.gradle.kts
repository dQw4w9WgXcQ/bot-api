plugins {
    id("java")
}

group = "github.dqw4w9wgxcq.bot"
version = Versions.project

repositories {
    mavenCentral()
}

apply<MavenPublishPlugin>()

dependencies {
    compileOnly("com.google.code.gson:gson:${Versions.rlGson}")

    compileOnly("org.projectlombok:lombok:${Versions.rlLombok}")
    annotationProcessor("org.projectlombok:lombok:${Versions.rlLombok}")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testRuntimeOnly("com.google.code.gson:gson:${Versions.rlGson}")
}

tasks {
    java {
        withSourcesJar()
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    test {
        useJUnitPlatform()
    }
}

configure<PublishingExtension> {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
        }
    }
}
