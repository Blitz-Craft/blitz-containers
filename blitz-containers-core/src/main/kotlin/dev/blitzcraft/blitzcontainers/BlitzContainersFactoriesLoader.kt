package dev.blitzcraft.blitzcontainers

import java.net.URL
import java.util.*

internal object BlitzContainersFactoriesLoader {

  private val factories =
    BlitzContainersFactoriesLoader::class.java.classLoader.getResources("META-INF/blitzcontainers-factories.properties")
      .toList()
      .map {it.loadProperties()}
      .flatMap { props -> props.entries.map { it.key as String to it.value as String } }
      .groupBy({ it.first.trim() }, { it.second.trim() })

  fun <T> getFactories(factoryType: Class<T>) =
    factories.getOrDefault(factoryType.name, emptyList()).map { instantiateFactory(it, factoryType) }

  @Suppress("UNCHECKED_CAST")
  private fun <T> instantiateFactory(factoryName: String, parentType: Class<T>): T {
    try {
      val factoryClass = Class.forName(factoryName)
      require(parentType.isAssignableFrom(factoryClass)) { "Class [" + factoryName + "] is not assignable to factory type [" + parentType.name + "]" }
      return factoryClass.getDeclaredConstructor().newInstance() as T
    } catch (ex: Throwable) {
      throw IllegalArgumentException(
        "Unable to instantiate factory class [" + factoryName + "] for factory type [" + parentType.name + "]",
        ex
      )
    }
  }
}

private fun URL.loadProperties():Properties {
  val properties = Properties()
  properties.load(openStream())
  return properties
}