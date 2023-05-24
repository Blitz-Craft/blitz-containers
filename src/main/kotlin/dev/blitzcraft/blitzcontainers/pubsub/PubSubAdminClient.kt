package dev.blitzcraft.blitzcontainers.pubsub

import com.google.api.gax.core.NoCredentialsProvider
import com.google.api.gax.grpc.GrpcTransportChannel
import com.google.api.gax.rpc.FixedTransportChannelProvider
import com.google.cloud.pubsub.v1.SubscriptionAdminClient
import com.google.cloud.pubsub.v1.SubscriptionAdminSettings
import com.google.cloud.pubsub.v1.TopicAdminClient
import com.google.cloud.pubsub.v1.TopicAdminSettings
import com.google.protobuf.Timestamp
import com.google.pubsub.v1.*
import io.grpc.ManagedChannelBuilder
import java.time.Instant

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

  fun topics() = topicAdminClient.listTopics(ProjectName.of(projectId)).iterateAll().map { it.name }.toSet()

  fun subscriptions(topic: String) =
    topicAdminClient.listTopicSubscriptions(TopicName.of(projectId, topic))
      .iterateAll()
      .toSet()


  fun createTopic(topic: String) {
    topicAdminClient.createTopic(TopicName.of(projectId, topic))
  }

  fun acknowledgeAllMessages(subscription: String) {
    subscriptionAdminClient.seek(SeekRequest.newBuilder()
                                   .setSubscription(SubscriptionName.of(projectId, subscription).toString())
                                   .setTime(Timestamp
                                              .newBuilder()
                                              .setSeconds(Instant.now().epochSecond)
                                              .setNanos(Instant.now().nano))
                                   .build())
  }

  fun createSubscriptions(topic: String, subscriptions: Set<String>) {
    subscriptions.forEach {
      subscriptionAdminClient.createSubscription(
        SubscriptionName.of(projectId, it),
        TopicName.of(projectId, topic),
        PushConfig.getDefaultInstance(),
        10
      )
    }
  }

  override fun close() {
    channel.shutdownNow()
    topicAdminClient.close()
    subscriptionAdminClient.close()
  }
}