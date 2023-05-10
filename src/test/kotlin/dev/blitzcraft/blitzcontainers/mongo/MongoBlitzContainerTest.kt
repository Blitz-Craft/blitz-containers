package dev.blitzcraft.blitzcontainers.mongo

import com.mongodb.MongoCommandException
import com.mongodb.reactivestreams.client.MongoClients
import org.assertj.core.api.Assertions.assertThat
import org.bson.Document
import org.junit.jupiter.api.Test
import reactor.kotlin.core.publisher.toMono
import reactor.test.StepVerifier

class MongoBlitzContainerTest {

  @Test
  fun `Spring properties contains key for connecting to MongoDB`() {
    // given
    val mongoBlitzContainer = MongoBlitzContainer(Mongo(tag = "6"))
    // when
    mongoBlitzContainer.start()
    // then
    assertThat(mongoBlitzContainer.springProperties()).containsKey("spring.data.mongodb.uri")
  }

  @Test
  fun `should activate notablescan`() {
    // given
    val mongoBlitzContainer = MongoBlitzContainer(Mongo(tag = "6", isNoTableScan = true))
    mongoBlitzContainer.start()
    val client = MongoClients.create(mongoBlitzContainer.springProperties()["spring.data.mongodb.uri"])
    val collection = client.getDatabase("test").getCollection("mycollection")
    // when
    val executionPlan = collection.insertOne(Document("name", "Ilya")).toMono()
      .then(collection.find(Document("name", "Ilya")).explain().toMono())
    // then
    StepVerifier.create(executionPlan)
      .expectErrorMatches {
        (it as MongoCommandException).response["codeName"]!!.asString().value == "NoQueryExecutionPlans"
      }
      .verify()
  }
}