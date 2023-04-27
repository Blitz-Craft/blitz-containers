package dev.blitzcraft.blitzcontainers.pubsub

import com.google.api.gax.core.NoCredentialsProvider
import com.google.api.gax.grpc.GrpcTransportChannel
import com.google.api.gax.rpc.FixedTransportChannelProvider
import com.google.cloud.pubsub.v1.SubscriptionAdminClient
import com.google.cloud.pubsub.v1.SubscriptionAdminSettings
import com.google.cloud.pubsub.v1.TopicAdminClient
import com.google.cloud.pubsub.v1.TopicAdminSettings
import com.google.pubsub.v1.PushConfig
import com.google.pubsub.v1.SubscriptionName
import com.google.pubsub.v1.TopicName
import io.grpc.ManagedChannelBuilder

internal data class PubSubAdminClient(
  private val emulatorHost: String,
  private val projectId: String
): AutoCloseable {

  private val channel = ManagedChannelBuilder.forTarget(emulatorHost).usePlaintext().build()
  private val topicAdminClient = TopicAdminClient.create(
    TopicAdminSettings
      .newBuilder()
      .setTransportChannelProvider(FixedTransportChannelProvider.create(GrpcTransportChannel.create(channel)))
      .setCredentialsProvider(NoCredentialsProvider.create())
      .build())
  private val subscriptionAdminClient = SubscriptionAdminClient.create(
    SubscriptionAdminSettings
      .newBuilder()
      .setTransportChannelProvider(FixedTransportChannelProvider.create(GrpcTransportChannel.create(channel)))
      .setCredentialsProvider(NoCredentialsProvider.create())
      .build())

  fun createTopic(topic: String) {
    topicAdminClient.createTopic(TopicName.of(projectId, topic))
  }

  fun deleteTopic(topic: String) {
    topicAdminClient.deleteTopic(TopicName.of(projectId, topic))
  }

  fun createSubscriptions(topic: String, subscriptions: List<Subscription>) {
    subscriptions.forEach {
      subscriptionAdminClient.createSubscription(
        SubscriptionName.of(projectId, it.name),
        TopicName.of(projectId, topic),
        PushConfig.getDefaultInstance(),
        10
      )
    }
  }

  fun deleteSubscriptions(subscriptions: List<String>) {
    subscriptions.forEach { subscriptionAdminClient.deleteSubscription(SubscriptionName.of(projectId, it)) }
  }

  override fun close() {
    channel.shutdownNow()
    topicAdminClient.close()
    subscriptionAdminClient.close()
  }
}