package dev.blitzcraft.blitzcontainers

import dev.blitzcraft.blitzcontainers.mongo.Mongo
import dev.blitzcraft.blitzcontainers.mongo.MongoBlitzContainer
import dev.blitzcraft.blitzcontainers.pubsub.PubSub
import dev.blitzcraft.blitzcontainers.pubsub.PubSubBlitzContainer
import org.testcontainers.lifecycle.Startables
import java.util.concurrent.ConcurrentHashMap

internal object BlitzContainerManager {

  private val containersCache = ConcurrentHashMap<String, BlitzContainer<*, *>>()
  private val containersProvider: Map<Class<out Annotation>, (Annotation) -> BlitzContainer<*, *>> = mapOf(
    Mongo::class.java to { MongoBlitzContainer(it as Mongo) },
    PubSub::class.java to { PubSubBlitzContainer(it as PubSub) }
  )

  @JvmStatic
  fun startOrReuseContainersFor(testClass: Class<*>): Map<String, Any> {
    return startOrReuseContainersFor(*testClass.annotations)
  }

  @JvmStatic
  fun startOrReuseContainersFor(vararg annotations: Annotation): Map<String, Any> {
    val blitzContainers = annotations
      .filter { containersProvider.containsKey(it.annotationClass.java) }
      .map { containersProvider[it.annotationClass.java]!!.invoke(it) }

    Startables.deepStart(blitzContainers.filterNot { containersCache.containsKey(it.key()) }).join()

    return blitzContainers
      .map { container -> containersCache.computeIfAbsent(container.key()) { container } }
      .fold(mapOf()) { props, container -> props + container.springProperties() }
  }

  @JvmStatic
  internal fun clearCache() {
    containersCache.clear()
  }
}