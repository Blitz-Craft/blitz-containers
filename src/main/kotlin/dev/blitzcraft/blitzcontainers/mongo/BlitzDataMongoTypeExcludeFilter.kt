package dev.blitzcraft.blitzcontainers.mongo

import org.springframework.boot.test.autoconfigure.filter.StandardAnnotationCustomizableTypeExcludeFilter

internal class BlitzDataMongoTypeExcludeFilter(testClass: Class<*>) :
    StandardAnnotationCustomizableTypeExcludeFilter<BlitzDataMongoTest>(testClass)
