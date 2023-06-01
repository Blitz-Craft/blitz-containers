package dev.blitzcraft.blitzcontainers

import org.springframework.test.context.TestContext

/**
 * Fixture allowing to set up test data before the test method execution
 * and to clean up after test method execution
 * A classpath scan is done by [BlitzContainerTestExecutionListener] to find all the fixtures
 */
interface BlitzContainersFixture<ANNOTATION: Annotation> {
  fun setup(annotation: ANNOTATION, testContext: TestContext)
  fun cleanup(annotation: ANNOTATION, testContext: TestContext)
}