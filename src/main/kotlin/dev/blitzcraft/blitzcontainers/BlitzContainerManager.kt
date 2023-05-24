package dev.blitzcraft.blitzcontainers

import dev.blitzcraft.blitzcontainers.mongo.Mongo
import dev.blitzcraft.blitzcontainers.mongo.MongoBlitzContainer
import dev.blitzcraft.blitzcontainers.pubsub.PubSub
import dev.blitzcraft.blitzcontainers.pubsub.PubSubBlitzContainer
import org.testcontainers.containers.GenericContainer
import org.testcontainers.lifecycle.Startables
import java.util.concurrent.ConcurrentHashMap

internal object BlitzContainerManager {

  private val containersCache = ConcurrentHashMap<String, BlitzContainer<Annotation, GenericContainer<*>>>()
  private val containersProvider: Map<Class<out Annotation>, (Annotation) -> BlitzContainer<Annotation, GenericContainer<*>>> =
    mapOf(
      Mongo::class.java to { MongoBlitzContainer(it as Mongo) },
      PubSub::class.java to { PubSubBlitzContainer(it as PubSub) }
    )

  fun startOrReuseContainersFor(testClass: Class<*>): Map<String, Any> {
    return startOrReuseContainersFor(*testClass.annotations)
  }

  fun startOrReuseContainersFor(vararg annotations: Annotation): Map<String, Any> {
    val managedContainers = annotations
      .filter { containersProvider.containsKey(it.annotationClass.java) }
      .associateWith { containersProvider[it.annotationClass.java]!!.invoke(it) }

    Startables.deepStart(managedContainers.values.filterNot { containersCache.containsKey(it.key) }).join()

    managedContainers.forEach { it.value.prepareForTest(it.key) }

    return managedContainers.values
      .map { container -> containersCache.computeIfAbsent(container.key) { container } }
      .fold(mapOf()) { props, container -> props + container.springProperties() }
  }

  internal fun clearCache() {
    containersCache.clear()
  }
}