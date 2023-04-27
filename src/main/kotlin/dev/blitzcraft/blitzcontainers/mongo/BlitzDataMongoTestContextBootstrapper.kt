package dev.blitzcraft.blitzcontainers.mongo

import dev.blitzcraft.blitzcontainers.BlitzContainerManager
import dev.blitzcraft.blitzcontainers.findAnnotation
import org.springframework.boot.test.context.SpringBootTestContextBootstrapper

internal class BlitzDataMongoTestContextBootstrapper: SpringBootTestContextBootstrapper() {

  override fun getProperties(testClass: Class<*>): Array<String> {
    val annotation = testClass.findAnnotation(BlitzDataMongoTest::class.java)
    return annotation.properties +
           BlitzContainerManager.startOrReuseContainersFor(annotation.mongo).map { "${it.key}=${it.value}" }
  }
}
