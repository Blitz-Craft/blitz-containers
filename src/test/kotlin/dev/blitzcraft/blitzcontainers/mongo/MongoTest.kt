package dev.blitzcraft.blitzcontainers.mongo

import com.mongodb.reactivestreams.client.MongoClients
import org.bson.BsonDocument
import org.bson.BsonString
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.env.Environment
import reactor.test.StepVerifier

@SpringBootTest
@Mongo(tag = "5", isNoTableScan = true)
class MongoTest {

  @Autowired
  private lateinit var environment: Environment

  @Test
  fun `Verify MongoDB is up and running`() {
    // given
    val mongoClient = MongoClients.create(environment.getProperty("spring.data.mongodb.uri"))
    // when
    val command = mongoClient.getDatabase("test").runCommand(BsonDocument("ping", BsonString("1")))
    // then
    StepVerifier.create(command)
      .expectNextMatches { it.containsKey("ok") }
      .verifyComplete()
  }
}