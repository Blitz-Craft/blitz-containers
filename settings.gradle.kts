rootProject.name = "blitz-containers"

dependencyResolutionManagement {
  versionCatalogs {
    create("libs") {
      version("testcontainers", "1.18.0")
    }
  }
}

include(
  "blitz-containers-core",
  "blitz-containers-mariadb",
  "blitz-containers-mongo",
  "blitz-containers-mysql",
  "blitz-containers-postgresql",
  "blitz-containers-pubsub")
