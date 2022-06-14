package dev.blitzcraft.blitzcontainers.mongo

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo
import org.springframework.boot.test.autoconfigure.filter.TypeExcludeFilters
import org.springframework.context.annotation.ComponentScan
import org.springframework.core.env.Environment
import org.springframework.test.context.BootstrapWith
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.lang.annotation.Inherited

@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Inherited
@AutoConfigureCache
@AutoConfigureDataMongo
@ExtendWith(SpringExtension::class)
@BootstrapWith(BlitzDataMongoTestContextBootstrapper::class)
@TypeExcludeFilters(BlitzDataMongoTypeExcludeFilter::class)
@OverrideAutoConfiguration(enabled = false)
@ImportAutoConfiguration
annotation class BlitzDataMongoTest(

    /**
     * Properties in form key=value that should be added to the Spring
     * [Environment] before the test runs.
     *
     * @return the properties to add
     */
    val properties: Array<String> = [],

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
    val isNoTableScan: Boolean = false,

    /**
     * Determines if default filtering should be used with
     * {@link SpringBootApplication @SpringBootApplication}. By default no beans are
     * included.
     *
     * @return if default filters should be used
     * @see #includeFilters()
     * @see #excludeFilters()
     */
    val useDefaultFilter: Boolean = false,


    /**
     * A set of include filters which can be used to add otherwise filtered beans to the
     * application context.
     *
     * @return include filters to apply
     */
    val includeFilter: Array<ComponentScan.Filter> = [],

    /**
     * A set of exclude filters which can be used to filter beans that would otherwise be
     * added to the application context.
     *
     * @return exclude filters to apply
     */
    val excludeFilter: Array<ComponentScan.Filter> = []


)
