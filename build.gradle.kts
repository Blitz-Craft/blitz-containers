import java.time.Duration

plugins {
  id("java-library")
  id("maven-publish")
  id("signing")
  id("org.springframework.boot") version "2.7.12"
  id("io.spring.dependency-management") version "1.1.0"
  id("com.adarshr.test-logger") version "3.2.0"
  id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
  id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.7"
  kotlin("jvm") version "1.7.0"
  kotlin("plugin.spring") version "1.7.0"
}

description = "The project uniting the SpringBoot tests with Testcontainers"

allprojects {
  group = "dev.blitzcraft"
  version = System.getenv("RELEASE_VERSION") ?: "LOCAL-SNAPSHOT"
  repositories {
    mavenCentral()
  }
}

subprojects {
  apply(plugin = "java-library")
  apply(plugin = "maven-publish")
  apply(plugin = "version-catalog")
  apply(plugin = "signing")
  apply(plugin = "org.springframework.boot")
  apply(plugin = "io.spring.dependency-management")
  apply(plugin = "com.adarshr.test-logger")
  apply(plugin = "org.jetbrains.kotlin.jvm")
  apply(plugin = "org.jetbrains.kotlin.plugin.spring")
  apply(plugin = "org.jetbrains.gradle.plugin.idea-ext")

  java.sourceCompatibility = JavaVersion.VERSION_11

  dependencies {
    implementation("org.springframework:spring-test")
    implementation("org.springframework.boot:spring-boot-test")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
  }

  tasks.bootJar {
    enabled = false
  }

  tasks.jar {
    archiveClassifier.set("")
  }

  tasks.withType<Test> {
    useJUnitPlatform()
    if (project.name != "blitz-containers-core") {
      dependsOn(":blitz-containers-core:test")
      maxParallelForks = Runtime.getRuntime().availableProcessors()
    }
  }

  java {
    withSourcesJar()
    withJavadocJar()
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
  signing {
    val signingKey: String? by project
    val signingPassphrase: String? by project
    useInMemoryPgpKeys(signingKey, signingPassphrase)
    sign(publishing.publications["jars"])
  }
}

nexusPublishing {
  this.repositories {
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