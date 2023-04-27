package dev.blitzcraft.blitzcontainers

import org.springframework.test.context.TestContextAnnotationUtils


internal fun <T: Annotation> Class<*>.findAnnotation(annotationClass: Class<T>) =
  requireNotNull(TestContextAnnotationUtils.findMergedAnnotation(this, annotationClass))