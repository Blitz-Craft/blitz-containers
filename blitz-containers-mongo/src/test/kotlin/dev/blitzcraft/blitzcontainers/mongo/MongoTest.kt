package dev.blitzcraft.blitzcontainers.mongo

import com.mongodb.reactivestreams.client.MongoClients
import org.assertj.core.api.Assertions.assertThat
import org.bson.BsonDocument
import org.bson.BsonString
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.env.Environment
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.test.StepVerifier

@SpringBootTest
@Mongo(tag = "6", isNoTableScan = true)
class MongoTest {

  @Autowired
  private lateinit var environment: Environment

  @Autowired
  private lateinit var repo: TestRepository

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

  @Test
  fun `should save several documents`() {
    // given
    val documents = listOf(TestDocument("Ilya"), TestDocument("Tof"))
    // when
    val savedDocuments = repo.saveAll(documents)
    // then
    StepVerifier.create(savedDocuments)
      .recordWith(::ArrayList)
      .expectNextCount(2)
      .consumeRecordedWith {
        assertThat(it).containsExactlyInAnyOrder(TestDocument("Ilya"), TestDocument("Tof"))
      }
      .verifyComplete()
  }
}

data class TestDocument(val name: String)
interface TestRepository: ReactiveMongoRepository<TestDocument, ObjectId>