import java.time.Duration

plugins {
  id("java-library")
  id("maven-publish")
  id("groovy")
  id("signing")
  id("org.springframework.boot") version "2.7.11"
  id("io.spring.dependency-management") version "1.0.11.RELEASE"
  id("com.adarshr.test-logger") version "3.2.0"
  id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
  kotlin("jvm") version "1.7.0"
  kotlin("plugin.spring") version "1.7.0"
}

group = "dev.blitzcraft"
description = "The project uniting the SpringBoot tests with Testcontainers"
version = System.getenv("RELEASE_VERSION") ?: "LOCAL-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

val testcontainersVersion by extra("1.17.6")
val spockVersion by extra("2.3-groovy-3.0")

dependencies {
  implementation("org.testcontainers:junit-jupiter:$testcontainersVersion")
  implementation("org.testcontainers:mongodb:$testcontainersVersion")
  implementation("org.testcontainers:gcloud:$testcontainersVersion")
  implementation("org.junit.jupiter:junit-jupiter-api")
  implementation("org.springframework:spring-test")
  implementation("org.springframework.boot:spring-boot-test")
  implementation("org.springframework.boot:spring-boot-test-autoconfigure")
  implementation("org.testcontainers:junit-jupiter")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("com.google.cloud:google-cloud-pubsub:1.123.6")

  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
  testImplementation("io.projectreactor:reactor-test")
  testImplementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
}

publishing {
  publications {
    create<MavenPublication>("jars") {
      from(components["java"])
      pom {
        name.set("Blitz-Containers")
        description.set("The project uniting the SpringBoot tests with Testcontainers")
        url.set("https://blitzcraft.dev")
        licenses {
          license {
            name.set("GNU GENERAL PUBLIC LICENSE, Version 3")
            url.set("https://gnu.org/licenses/gpl-3.0.txt")
          }
        }
        developers {
          developer {
            id.set("blitz-craft")
            name.set("BlitzCraft")
            email.set("contact@blitzcraft.dev")
          }
        }
        scm {
          connection.set("https://github.com/Blitz-Craft/blitz-containers.git")
          developerConnection.set("git@github.com:Blitz-Craft/blitz-containers.git")
          url.set("https://github.com/Blitz-Craft/blitz-containers")
        }
      }
    }
  }
}

nexusPublishing {
  repositories {
    sonatype {
      nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
      snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
    }
  }
  transitionCheckOptions {
    maxRetries.set(100)
    delayBetween.set(Duration.ofSeconds(5))
  }
}

signing {
  val signingKey: String? by project
  val signingPassphrase: String? by project
  useInMemoryPgpKeys(signingKey, signingPassphrase)
  sign(publishing.publications["jars"])
}

java {
  withSourcesJar()
  withJavadocJar()
}

repositories {
  mavenCentral()
}

tasks.bootJar {
  enabled = false
}

tasks.jar {
  archiveClassifier.set("")
}

tasks.test {
  useJUnitPlatform()
  filter {
    excludeTestsMatching("BlitzContainerManagerTest")
  }
  systemProperties["junit.jupiter.execution.parallel.enabled"] = "true"
  systemProperties["junit.jupiter.execution.parallel.mode.classes.default"] = "concurrent"
  systemProperties["junit.jupiter.execution.parallel.mode.default"] = "same_thread"
}

tasks.check{
  dependsOn("runBlitzContainerManagerTest")
}

tasks.register<Test>("runBlitzContainerManagerTest") {
  useJUnitPlatform()
  filter {
    includeTestsMatching("BlitzContainerManagerTest")
  }
}