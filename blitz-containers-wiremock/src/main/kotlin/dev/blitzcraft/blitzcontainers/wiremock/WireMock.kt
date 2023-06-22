package dev.blitzcraft.blitzcontainers.wiremock

import java.lang.annotation.Inherited

@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Inherited
annotation class WireMock(

  /**
   * The tag of the MongoDB image to start up.
   * Default value is 2.35.0-alpine
   *
   * @return the Docker image version
   */
  val tag: String = "2.35.0-alpine",

  /**
   *  The Spring property that holds the URL of the server to mock.
   *
   *  @return The Spring property that holds the URL of the server to mock
   */
  val serverUrlProperty: String,

  /**
   * Root directory under which **mappings** and **__files** reside as defined by Wiremock
   *
   * @return Root directory under which **mappings** and **__files** reside
   */
  val stubsLocation: String,

  /**
   * Directory of WireMock Extensions packaged as JAR
   *
   * @return Directory of WireMock Extensions packaged as JAR
   */
  val extensionsLocation: String = "",

  /**
   *  Wiremock Extension class name to register
   *
   * @return WireMock Extension class name to register
   */
  val extensionClasses: Array<String> = []
)
