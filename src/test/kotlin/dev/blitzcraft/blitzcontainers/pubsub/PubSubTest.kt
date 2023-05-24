package dev.blitzcraft.blitzcontainers.pubsub

import com.google.cloud.spring.pubsub.core.PubSubTemplate
import com.google.cloud.spring.pubsub.reactive.PubSubReactiveFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.Duration

@SpringBootTest
@PubSub(
  tag = "emulators",
  topics = [Topic(name = "topic1", subscriptions = [Subscription(name = "sub1")])]
)
class PubSubTest {

  @Autowired
  lateinit var pubSubTemplate: PubSubTemplate

  @Autowired
  lateinit var reactiveFactory: PubSubReactiveFactory

  @Test
  fun `publish and consume a message`() {
    // given
    pubSubTemplate.publish("topic1", "Hello World !").get()

    // expect
    reactiveFactory.poll("sub1", 250L)
      .doOnNext { assertThat(it).isNotNull }
      .blockFirst(Duration.ofSeconds(3))
  }
}
