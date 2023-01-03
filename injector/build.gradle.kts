plugins {
    java
}

group = "github.dqw4w9wgxcq.bot"
version = Versions.project

repositories {
    mavenCentral()
}

apply<MavenPublishPlugin>()

dependencies {
    compileOnly("org.ow2.asm:asm:${Versions.asm}")
    compileOnly("org.ow2.asm:asm-tree:${Versions.asm}")
    compileOnly("org.slf4j:slf4j-api:${Versions.rlSlf4j}")
    compileOnly("org.projectlombok:lombok:${Versions.rlLombok}")
    annotationProcessor("org.projectlombok:lombok:${Versions.rlLombok}")
}

tasks {
    java {
        withSourcesJar()
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

configure<PublishingExtension> {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
        }
    }
}
