package dev.blitzcraft.blitzcontainers.pubsub

import com.google.cloud.spring.pubsub.core.PubSubTemplate
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@PubSub(
  tag = "emulators",
  topics = [Topic(name = "topic1", subscriptions = [Subscription(name = "sub1")])]
)
@TestMethodOrder(value = OrderAnnotation::class)
class PubSubTest {

  @Autowired
  lateinit var pubSubTemplate: PubSubTemplate

  @Test
  @Order(0)
  fun `publish and consume a message`() {
    // given
    pubSubTemplate.publish("topic1", "Hello World !").get()

    // expect
    assertThat(pubSubTemplate.pullAndAck("sub1", null, true)).hasSize(1)
  }

  @Test
  @Order(1)
  fun `publish a message without consume it`() {
    pubSubTemplate.publish("topic1", "Hello World !").get()
  }

  @Test
  @Order(2)
  fun `unconsumed message of previous is acknowledged before executing test method`() {
    assertThat(pubSubTemplate.pull("sub1", null, true)).isEmpty()
  }
}
