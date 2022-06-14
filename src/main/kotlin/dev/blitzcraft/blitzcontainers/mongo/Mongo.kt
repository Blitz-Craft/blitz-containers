package dev.blitzcraft.blitzcontainers.mongo

import java.lang.annotation.Inherited

@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Inherited
annotation class Mongo(

    /**
     * The version of the MongoDB image to start up.
     *
     * @return the Docker image version
     */
    val version: String,

    /**
     * Determines if Mongo Server config <code>notablescan</code> should be activated.
     *
     * @return if the feature should be active.
     */
    val isNoTableScan: Boolean = false
)
