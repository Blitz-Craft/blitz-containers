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
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.env.Environment
import java.util.concurrent.TimeUnit.SECONDS

@SpringBootTest
@PubSub(
  tag = "emulators",
  topics = [
    Topic(name = "topic1"),
    Topic(name = "topic2", subscriptions = [Subscription(name = "sub2a"), Subscription(name = "sub2b")])
  ]
)
class PubSubTest {

  @Autowired
  private lateinit var environment: Environment
  private lateinit var topicAdminClient: TopicAdminClient
  private lateinit var channel: ManagedChannel

  @BeforeEach
  fun setup() {
    channel = ManagedChannelBuilder
      .forTarget(environment.getProperty("spring.cloud.gcp.pubsub.emulator-host"))
      .usePlaintext()
      .build()
    val topicAdminSettings = TopicAdminSettings
      .newBuilder()
      .setTransportChannelProvider(FixedTransportChannelProvider.create(GrpcTransportChannel.create(channel)))
      .setCredentialsProvider(NoCredentialsProvider.create())
      .build()
    topicAdminClient = TopicAdminClient.create(topicAdminSettings)
  }

  @AfterEach
  fun cleanup() {
    channel.shutdownNow()
    channel.awaitTermination(1, SECONDS)
  }

  @Test
  fun `Creates automatically topics and subscriptions`() {
    // given
    val topics = topicAdminClient.listTopics(ProjectName.of("blitzcontainers-pubsub")).iterateAll().map { it.name }
    val topic1Subscriptions =
      topicAdminClient.listTopicSubscriptions(TopicName.of("blitzcontainers-pubsub", "topic1")).iterateAll().toList()
    val topic2Subscriptions =
      topicAdminClient.listTopicSubscriptions(TopicName.of("blitzcontainers-pubsub", "topic2")).iterateAll().toList()
    // expect
    assertThat(topics).containsExactlyInAnyOrder(
      TopicName.of("blitzcontainers-pubsub", "topic1").toString(),
      TopicName.of("blitzcontainers-pubsub", "topic2").toString()
    )
    assertThat(topic1Subscriptions).isEmpty()
    assertThat(topic2Subscriptions).containsExactlyInAnyOrder(
      SubscriptionName.of("blitzcontainers-pubsub", "sub2a").toString(),
      SubscriptionName.of("blitzcontainers-pubsub", "sub2b").toString()
    )
  }
}