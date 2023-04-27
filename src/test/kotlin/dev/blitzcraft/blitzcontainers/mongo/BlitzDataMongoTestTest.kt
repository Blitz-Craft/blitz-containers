package dev.blitzcraft.blitzcontainers.mongo

import com.mongodb.reactivestreams.client.MongoClients
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.bson.BsonDocument
import org.bson.BsonString
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Component
import reactor.test.StepVerifier

@BlitzDataMongoTest(mongo = Mongo(tag = "5", isNoTableScan = true))
class BlitzDataMongoTestTest {

  @Autowired
  private lateinit var applicationContext: ApplicationContext

  @Autowired
  private lateinit var repo: TestRepository

  @Test
  fun `Verify MongoDB is up and running`() {
    // given
    val mongoClient = MongoClients.create(applicationContext.environment.getProperty("spring.data.mongodb.uri"))
    val database = mongoClient.getDatabase("test")
    // when
    val command = database.runCommand(BsonDocument("ping", BsonString("1")))
    // then
    StepVerifier.create(command)
      .expectNextMatches { it.containsKey("ok") }
      .verifyComplete()
  }

  @Test
  fun `verify no other Spring Component is loaded`() {
    // expect
    assertThatThrownBy { applicationContext.getBean(DummySpringComponent::class.java) }
      .isInstanceOf(NoSuchBeanDefinitionException::class.java)
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
      .consumeRecordedWith { assertThat(it).containsExactlyInAnyOrder(TestDocument("Ilya"), TestDocument("Tof")) }
      .verifyComplete()
  }
}

@Component
class DummySpringComponent
data class TestDocument(val name: String)
interface TestRepository: ReactiveMongoRepository<TestDocument, ObjectId>