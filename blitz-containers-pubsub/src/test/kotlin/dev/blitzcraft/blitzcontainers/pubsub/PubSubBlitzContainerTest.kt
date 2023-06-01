package dev.blitzcraft.blitzcontainers.pubsub

import com.google.api.gax.core.NoCredentialsProvider
import com.google.api.gax.grpc.GrpcTransportChannel
import com.google.api.gax.rpc.FixedTransportChannelProvider
import com.google.cloud.pubsub.v1.TopicAdminClient
import com.google.cloud.pubsub.v1.TopicAdminSettings
import com.google.pubsub.v1.ProjectName
import com.google.pubsub.v1.SubscriptionName
import com.google.pubsub.v1.TopicName
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PubSubBlitzContainerTest {

  private lateinit var topicAdminClient: TopicAdminClient

  @Test
  fun `Spring properties contains key for connecting to PubSub`() {
    // given
    val pubSubBlitzContainer = PubSubBlitzContainer(PubSub(tag = "emulators"))
    // when
    pubSubBlitzContainer.start()
    // then
    assertThat(pubSubBlitzContainer.springProperties).containsKeys(
      "spring.cloud.gcp.pubsub.emulator-host",
      "spring.cloud.gcp.pubsub.project-id"
    )
  }

  @Test
  fun `Prepare container for test`() {
    // given
    val annotation = PubSub(
      tag = "emulators",
      topics = arrayOf(
        Topic(name = "Topic1", subscriptions = arrayOf(Subscription(name = "sub1")))
      )
    )
    val pubSubBlitzContainer = PubSubBlitzContainer(annotation)
    pubSubBlitzContainer.start()
    val channel = createChannel(pubSubBlitzContainer)
    topicAdminClient = topicAdminClient(channel)

    // when
    pubSubBlitzContainer.prepareForTest(annotation)

    // then
    val topics = topicAdminClient.listTopics(ProjectName.of("blitzcontainers-pubsub")).iterateAll().map { it.name }
    val subs =
      topicAdminClient.listTopicSubscriptions(TopicName.of("blitzcontainers-pubsub", "Topic1")).iterateAll().toList()

    assertThat(topics).containsExactlyInAnyOrder(TopicName.of("blitzcontainers-pubsub", "Topic1").toString())
    assertThat(subs).containsExactlyInAnyOrder(SubscriptionName.of("blitzcontainers-pubsub", "sub1").toString())

    // finally
    channel.shutdownNow()
  }

  private fun createChannel(pubSubBlitzContainer: PubSubBlitzContainer) =
    ManagedChannelBuilder
      .forTarget(pubSubBlitzContainer.springProperties["spring.cloud.gcp.pubsub.emulator-host"].toString())
      .usePlaintext()
      .build()

  private fun topicAdminClient(channel: ManagedChannel) =
    TopicAdminClient.create(TopicAdminSettings
                              .newBuilder()
                              .setTransportChannelProvider(FixedTransportChannelProvider.create(
                                GrpcTransportChannel.create(channel)))
                              .setCredentialsProvider(NoCredentialsProvider.create())
                              .build())
}