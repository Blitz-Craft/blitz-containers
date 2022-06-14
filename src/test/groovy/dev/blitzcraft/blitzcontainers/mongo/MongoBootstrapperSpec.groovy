package dev.blitzcraft.blitzcontainers.mongo


import spock.lang.Specification

import static org.hamcrest.Matchers.*
import static spock.util.matcher.HamcrestSupport.that

class MongoBootstrapperSpec extends Specification {

    def "Should start testcontainers and return Spring Data property"() {
        given:
        def properties = new MongoBlitzContainersBootstrapper().bootAndGetProperties(DummyTest)

        expect:
        that properties, hasEntry(equalTo('spring.data.mongodb.uri'), startsWith('mongodb://'))
    }

    @Mongo(version = "latest")
    private class DummyTest {}
}
