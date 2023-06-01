package dev.blitzcraft.blitzcontainers

import org.reflections.Reflections
import org.reflections.scanners.Scanners.SubTypes
import org.reflections.util.ClasspathHelper
import org.springframework.beans.BeanUtils
import org.testcontainers.containers.GenericContainer
import org.testcontainers.lifecycle.Startables
import java.lang.reflect.ParameterizedType
import java.util.concurrent.ConcurrentHashMap


internal object BlitzContainerManager {

  private val containersCache = ConcurrentHashMap<String, BlitzContainer<Annotation, GenericContainer<*>>>()

  @Suppress("UNCHECKED_CAST")
  private val factories =
    Reflections(ClasspathHelper.forJavaClassPath())
      .get(SubTypes.of(BlitzContainerFactory::class.java))
      .map { BeanUtils.instantiateClass(Class.forName(it)) as BlitzContainerFactory<Annotation, *> }
      .associateBy { it.annotationClass() }

  fun startOrReuseContainersFor(testClass: Class<*>): Map<String, Any> {
    return startOrReuseContainersFor(*testClass.annotations)
  }

  fun startOrReuseContainersFor(vararg annotations: Annotation): Map<String, Any> {
    val managedContainers = annotations
      .filter { factories.containsKey(it.annotationClass.java) }
      .associateWith { factories[it.annotationClass.java]!!.getContainer(it) }

    Startables.deepStart(managedContainers.values.filterNot { containersCache.containsKey(it.key) }).join()

    managedContainers.forEach { it.value.prepareForTest(it.key) }

    return managedContainers.values
      .map { container -> containersCache.computeIfAbsent(container.key) { container } }
      .fold(mapOf()) { props, container -> props + container.springProperties }
  }

  internal fun clearCache() {
    containersCache.clear()
  }

  private fun BlitzContainerFactory<*, *>.annotationClass(): Class<*> =
    Class.forName((javaClass.genericInterfaces[0] as ParameterizedType).actualTypeArguments[0].typeName)
}