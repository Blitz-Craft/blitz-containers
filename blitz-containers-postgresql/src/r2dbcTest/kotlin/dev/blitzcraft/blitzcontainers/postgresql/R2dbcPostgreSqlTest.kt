package dev.blitzcraft.blitzcontainers.postgresql

import io.r2dbc.spi.ConnectionFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.Resource
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.core.select
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.data.relational.core.mapping.Table
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator
import reactor.test.StepVerifier


@SpringBootTest
@PostgreSql(tag = "15.3-alpine")
class PostgreSqlR2dbcTest {

  @Autowired
  lateinit var personRepository: ReactivePersonRepository

  @Autowired
  lateinit var template: R2dbcEntityTemplate

  @BeforeEach
  fun populateTestData(@Value("classpath:r2dbc-init-script.sql") testDataSql: Resource,
                       @Autowired connectionFactory: ConnectionFactory) {
    val resourceDatabasePopulator = ResourceDatabasePopulator()
    resourceDatabasePopulator.addScript(testDataSql)
    resourceDatabasePopulator.populate(connectionFactory).block()
  }

  @Test
  fun `Verify PostgreSQL is up and running`() {
    // when
    val version = template.databaseClient.sql("SELECT 1;")
      .map { row, _ -> row.get(0, String::class.java) }
      .one()
    // then
    StepVerifier.create(version)
      .assertNext { assertThat(it).isEqualTo("1") }
      .verifyComplete()
  }

  @Test
  fun `Save multiple Jpa Entities`() {
    // when
    val persons = personRepository.saveAll(listOf(ReactivePerson(1, "Ilya"), ReactivePerson(2, "Tof")))
    // then
    StepVerifier.create(persons.then(template.select<ReactivePerson>().count()))
      .assertNext { assertThat(it).isEqualTo(2) }
      .verifyComplete()
  }
}

@Table("r2dbc_person")
class ReactivePerson(
  val id: Long,
  val name: String
)

interface ReactivePersonRepository: R2dbcRepository<ReactivePerson, Long>
