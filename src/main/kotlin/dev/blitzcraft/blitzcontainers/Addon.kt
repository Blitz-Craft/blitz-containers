package dev.blitzcraft.blitzcontainers

import dev.blitzcraft.blitzcontainers.ContainersCache.bootOrReuseCompatibleContainer
import dev.blitzcraft.blitzcontainers.mongo.Mongo
import dev.blitzcraft.blitzcontainers.mongo.MongoBlitzContainersBootstrapper
import org.springframework.test.context.TestContextAnnotationUtils

internal enum class Addon(
  private val activatedByAnnotation: Class<out Annotation>,
  private val bootstrapper: BlitzContainersBootstrapper
) {
  MONGO(Mongo::class.java, MongoBlitzContainersBootstrapper());

  private fun isActivatedOn(clazz: Class<*>) =
    TestContextAnnotationUtils.hasAnnotation(clazz, activatedByAnnotation)

  private fun obtainContainerProperties(testClass: Class<*>) =
    bootOrReuseCompatibleContainer(bootstrapper.containerKey(testClass)) {
      bootstrapper.bootAndGetProperties(testClass)
    }

  companion object {
    private fun activeAddons(testClass: Class<*>): List<Addon> = values().filter { it.isActivatedOn(testClass) }

    @JvmStatic
    fun detectActiveAddonsAndReturnProperties(testClass: Class<*>): Map<String, Any> =
      activeAddons(testClass)
        .flatMap { it.obtainContainerProperties(testClass).toList() }
        .toMap()
  }
}
