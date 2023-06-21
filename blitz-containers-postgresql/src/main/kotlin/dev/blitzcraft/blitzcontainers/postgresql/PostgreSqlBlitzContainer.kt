package dev.blitzcraft.blitzcontainers.postgresql

import dev.blitzcraft.blitzcontainers.BlitzContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import java.time.Duration

internal class PostgreSqlBlitzContainer(annotation: PostgreSql):
    BlitzContainer<PostgreSql, PostgreSQLContainer<*>>(annotation) {

  override fun generateSpringProperties(annotation: PostgreSql) =
    if (isR2dbcInClasspath()) r2dbcProperties()
    else jdbcProperties()

  private fun jdbcProperties() =
    mapOf(
      "spring.test.database.replace" to "NONE",
      "spring.datasource.url" to container.jdbcUrl,
      "spring.datasource.username" to container.username,
      "spring.datasource.password" to container.password
    )

  private fun r2dbcProperties() =
    mapOf("spring.r2dbc.url" to container.jdbcUrl.replace("jdbc", "r2dbc"),
          "spring.r2dbc.username" to container.username,
          "spring.r2dbc.password" to container.password
    )


  override fun createContainer(annotation: PostgreSql) =
    PostgreSQLContainer(DockerImageName
                          .parse("postgres")
                          .withTag(annotation.tag)).withStartupTimeout(Duration.ofMinutes(3))

  override fun generateKey(annotation: PostgreSql) =
    "${annotation.annotationClass.java.simpleName}/${annotation.tag}"

  override fun prepareForTest(annotation: PostgreSql) {
    /*no op*/
  }

  private fun isR2dbcInClasspath() =
    try {
      Class.forName("io.r2dbc.postgresql.PostgresqlConnectionConfiguration")
      true
    } catch (e: Exception) {
      false
    }
}

