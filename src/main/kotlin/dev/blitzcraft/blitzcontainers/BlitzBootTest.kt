package dev.blitzcraft.blitzcontainers

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.AliasFor
import org.springframework.core.env.Environment
import org.springframework.test.context.BootstrapWith
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.lang.annotation.Inherited
import kotlin.reflect.KClass


@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Inherited
@BootstrapWith(BlitzBootTestContextBootstrapper::class)
@ExtendWith(SpringExtension::class)
@ImportAutoConfiguration
@ContextConfiguration(loader = BlitzBootContextLoader::class)
annotation class BlitzBootTest(

  /**
   * Properties in form key=value that should be added to the Spring
   * [Environment][Environment] before the test runs.
   *
   * @return the properties to add
   */
  val properties: Array<String> = [],


  /**
   * The *component classes* to use for loading an
   * [ApplicationContext][ApplicationContext]. Can also
   * be specified using
   * [@ContextConfiguration(classes=...)][ContextConfiguration.classes]. If no
   * explicit classes are defined the test will look for nested
   * [@Configuration][Configuration] classes, before falling back to a
   * [@SpringBootConfiguration][SpringBootConfiguration] search.cd
   *
   * @return the component classes used to load the application context
   * @see ContextConfiguration.classes
   */
  @get: AliasFor("value")
  val classes: Array<KClass<*>> = [],

  @get: AliasFor("classes")
  val value: Array<KClass<*>> = [],

  val webEnvironment: SpringBootTest.WebEnvironment = SpringBootTest.WebEnvironment.MOCK,

  /**
   * Auto-configuration exclusions that should be applied for this test.
   *
   * @return auto-configuration exclusions to apply
   */
  @get: AliasFor(annotation = ImportAutoConfiguration::class, attribute = "exclude")
  val excludeAutoConfiguration: Array<KClass<*>> = []
)
