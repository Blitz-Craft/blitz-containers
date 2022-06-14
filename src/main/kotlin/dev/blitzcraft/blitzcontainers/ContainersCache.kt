package dev.blitzcraft.blitzcontainers

import java.util.concurrent.ConcurrentHashMap


internal object ContainersCache {

  private val CONTAINER_PROPS = ConcurrentHashMap<String, Map<String, String>>()

  fun bootOrReuseCompatibleContainer(
    containerKey: String,
    bootAndReturnProperties: () -> Map<String, String>
  ) =
    CONTAINER_PROPS.computeIfAbsent(containerKey) { bootAndReturnProperties.invoke() }
}