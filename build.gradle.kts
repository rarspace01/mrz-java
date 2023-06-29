import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.22"
    application
    java
    `maven-publish`
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.3")
    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation("io.mockk:mockk:1.13.5")
    testImplementation("org.assertj:assertj-core:3.24.2")
}

group = "com.github.bordertech.mrz"
version = "0.9-SNAPSHOT"
description = "MRZ Java Parser"
java.sourceCompatibility = JavaVersion.VERSION_1_8

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    withJavadocJar()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
        pom {
            name.set("My Library")
            description.set("A concise description of my library")
            url.set("http://www.example.com/library")
            properties.set(mapOf(
                "myProp" to "value",
                "prop.with.dots" to "anotherValue"
            ))
            licenses {
                license {
                    name.set("The Apache License, Version 2.0")
                    url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                }
            }
            developers {
                developer {
                    id.set("johnd")
                    name.set("John Doe")
                    email.set("john.doe@example.com")
                }
            }
            scm {
                connection.set("scm:git:git://example.com/my-library.git")
                developerConnection.set("scm:git:ssh://example.com/my-library.git")
                url.set("http://example.com/my-library/")
            }
        }
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}
