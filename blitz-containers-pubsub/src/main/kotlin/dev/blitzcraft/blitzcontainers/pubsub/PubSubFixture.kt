package dev.blitzcraft.blitzcontainers.pubsub

import dev.blitzcraft.blitzcontainers.BlitzContainersFixture
import org.springframework.core.env.get
import org.springframework.test.context.TestContext

class PubSubFixture: BlitzContainersFixture<PubSub> {

  override fun setup(annotation: PubSub, testContext: TestContext) {
    // no op
  }

  override fun cleanup(annotation: PubSub, testContext: TestContext) {
    val emulatorHost = testContext.applicationContext.environment["spring.cloud.gcp.pubsub.emulator-host"]!!
    val projectId = testContext.applicationContext.environment["spring.cloud.gcp.pubsub.project-id"]!!

    PubSubAdminClient(emulatorHost, projectId).use {
      annotation.topics
        .flatMap { it.subscriptions.toList() }
        .map { it.name }
        .forEach { sub -> it.acknowledgeAllMessages(sub) }
    }
  }
}