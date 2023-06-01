package dev.blitzcraft.blitzcontainers

import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.env.MapPropertySource
import org.springframework.test.context.ContextConfigurationAttributes
import org.springframework.test.context.ContextCustomizer
import org.springframework.test.context.ContextCustomizerFactory
import org.springframework.test.context.MergedContextConfiguration

internal class BlitzContainerContextCustomizerFactory: ContextCustomizerFactory {
  override fun createContextCustomizer(testClass: Class<*>, configAttributes: List<ContextConfigurationAttributes>) =
    BlitzContainerContextCustomizer(BlitzContainerManager.startOrReuseContainersFor(testClass))
}

internal class BlitzContainerContextCustomizer(private val addonsProperties: Map<String, Any>): ContextCustomizer {
  override fun customizeContext(context: ConfigurableApplicationContext, mergedConfig: MergedContextConfiguration) {
     context.environment.propertySources.addFirst(MapPropertySource("blitzContainersPropertySource", addonsProperties))
  }
}