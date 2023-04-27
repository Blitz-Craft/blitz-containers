package dev.blitzcraft.blitzcontainers.pubsub

import dev.blitzcraft.blitzcontainers.BlitzContainersFixture
import dev.blitzcraft.blitzcontainers.findAnnotation
import org.springframework.core.env.get
import org.springframework.test.context.TestContext

internal object PubSubFixture: BlitzContainersFixture {

  override fun setup(testContext: TestContext) {
    val emulatorHost = testContext.applicationContext.environment["spring.cloud.gcp.pubsub.emulator-host"]!!
    val projectId = testContext.applicationContext.environment["spring.cloud.gcp.pubsub.project-id"]!!
    val topicsAndSubscriptions = topicsAndSubscriptions(testContext.testClass.findAnnotation(PubSub::class.java).topics)

    PubSubAdminClient(emulatorHost, projectId).use {
      topicsAndSubscriptions.keys.forEach { topic -> it.createTopic(topic) }
      topicsAndSubscriptions.forEach { (topic, subs) -> it.createSubscriptions(topic, subs) }
    }
  }

  override fun cleanup(testContext: TestContext) {
    val emulatorHost = testContext.applicationContext.environment["spring.cloud.gcp.pubsub.emulator-host"]!!
    val projectId = testContext.applicationContext.environment["spring.cloud.gcp.pubsub.project-id"]!!
    val topicsAndSubscriptions = topicsAndSubscriptions(testContext.testClass.findAnnotation(PubSub::class.java).topics)

    PubSubAdminClient(emulatorHost, projectId).use {
      topicsAndSubscriptions.values.forEach { subs -> it.deleteSubscriptions(subs.map { it.name }) }
      topicsAndSubscriptions.keys.forEach { topic -> it.deleteTopic(topic) }
    }
  }

  private fun topicsAndSubscriptions(topics: Array<Topic>) =
    topics.associate { it.name to it.subscriptions.toList() }
}