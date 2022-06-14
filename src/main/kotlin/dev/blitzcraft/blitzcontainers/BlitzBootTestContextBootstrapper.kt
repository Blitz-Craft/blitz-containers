package dev.blitzcraft.blitzcontainers

import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.context.SpringBootTestContextBootstrapper
import org.springframework.test.context.TestContextAnnotationUtils

internal class BlitzBootTestContextBootstrapper: SpringBootTestContextBootstrapper() {
  override fun getProperties(testClass: Class<*>): Array<String> {
    return getBlitzSpringBootTestAnnotation(testClass).properties
  }

  override fun getClasses(testClass: Class<*>): Array<Class<*>> {
    return getBlitzSpringBootTestAnnotation(testClass).classes
      .map { it.javaObjectType }
      .toTypedArray()
  }

  override fun getWebEnvironment(testClass: Class<*>): WebEnvironment {
    return getBlitzSpringBootTestAnnotation(testClass).webEnvironment
  }

  private fun getBlitzSpringBootTestAnnotation(testClass: Class<*>): BlitzBootTest {
    return TestContextAnnotationUtils.findMergedAnnotation(testClass, BlitzBootTest::class.java)!!
  }
}