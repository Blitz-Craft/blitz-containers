package dev.blitzcraft.blitzcontainers

import dev.blitzcraft.blitzcontainers.pubsub.PubSub
import dev.blitzcraft.blitzcontainers.pubsub.PubSubFixture
import org.springframework.test.context.TestContext
import org.springframework.test.context.TestExecutionListener

class BlitzContainerTestExecutionListener: TestExecutionListener {

  private val fixtures: Map<Class<out Annotation>, BlitzContainersFixture> = mapOf(
    PubSub::class.java to PubSubFixture
  )

  override fun beforeTestMethod(testContext: TestContext) {
    testContext.testClass.annotations.toList()
      .filter { fixtures.containsKey(it.annotationClass.java) }
      .forEach { fixtures[it.annotationClass.java]!!.setup(testContext) }
  }

  override fun afterTestMethod(testContext: TestContext) {
    testContext.testClass.annotations.toList()
      .filter { fixtures.containsKey(it.annotationClass.java) }
      .forEach { fixtures[it.annotationClass.java]!!.cleanup(testContext) }
  }
}