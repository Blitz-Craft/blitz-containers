package dev.blitzcraft.blitzcontainers

internal interface BlitzContainersBootstrapper {
  fun bootAndGetProperties(testClass: Class<*>): Map<String, String>
  fun containerKey(testClass: Class<*>): String
}