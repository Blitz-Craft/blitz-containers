package dev.blitzcraft.blitzcontainers.postgresql

import java.lang.annotation.Inherited

@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Inherited
annotation class PostgreSql(

  /**
   * The tag of the Postgres image to start up.
   *
   * @return the Docker image version
   */
  val tag: String,
)
