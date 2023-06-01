package dev.blitzcraft.blitzcontainers.mariadb

import java.lang.annotation.Inherited

@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Inherited
annotation class MariaDb(

  /**
   * The tag of the MariaDB image to start up.
   *
   * @return the Docker image version
   */
  val tag: String,
)
