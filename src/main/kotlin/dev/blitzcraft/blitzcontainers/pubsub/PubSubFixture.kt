package dev.blitzcraft.blitzcontainers.pubsub

import dev.blitzcraft.blitzcontainers.BlitzContainersFixture
import dev.blitzcraft.blitzcontainers.findAnnotation
import org.springframework.core.env.get
import org.springframework.test.context.TestContext

internal object PubSubFixture: BlitzContainersFixture {
  override fun setup(testContext: TestContext) {
    // nothing to do
  }

  override fun cleanup(testContext: TestContext) {
    val emulatorHost = testContext.applicationContext.environment["spring.cloud.gcp.pubsub.emulator-host"]!!
    val projectId = testContext.applicationContext.environment["spring.cloud.gcp.pubsub.project-id"]!!

    PubSubAdminClient(emulatorHost, projectId).use {
      testContext.testClass.findAnnotation(PubSub::class.java).topics
        .flatMap { it.subscriptions.toList() }
        .map { it.name }
        .forEach { sub -> it.acknowledgeAllMessages(sub) }
    }
  }
}