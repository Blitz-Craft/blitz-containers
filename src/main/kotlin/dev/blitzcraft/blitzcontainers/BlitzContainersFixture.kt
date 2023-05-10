package dev.blitzcraft.blitzcontainers

import org.springframework.test.context.TestContext

internal interface BlitzContainersFixture {
  fun setup(testContext: TestContext)
  fun cleanup(testContext: TestContext)
}
