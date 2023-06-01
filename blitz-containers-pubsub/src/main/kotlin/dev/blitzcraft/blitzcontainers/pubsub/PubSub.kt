package dev.blitzcraft.blitzcontainers.pubsub

import java.lang.annotation.Inherited

@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Inherited
annotation class PubSub(

  /**
   * The tag of the Google cloud-sdk image to start up.
   *
   * @return the Docker image version
   */
  val tag: String,

  /**
   * List of the Topics which will be created once the container is initialized.
   */
  val topics: Array<Topic> = []
)

annotation class Topic(
  /**
   * Topic name
   */
  val name: String,

  /**
   * Name of the associated subscriptions.
   */
  val subscriptions: Array<Subscription> = []
)

annotation class Subscription(
  /**
   * Subscription name
   */
  val name: String,

  /**
   * Name of the associated subscriptions.
   */
)