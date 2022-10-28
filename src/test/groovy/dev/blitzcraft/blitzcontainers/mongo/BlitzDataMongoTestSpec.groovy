package dev.blitzcraft.blitzcontainers.mongo

import com.mongodb.reactivestreams.client.MongoClients
import org.bson.BsonDocument
import org.bson.BsonString
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import reactor.test.StepVerifier
import spock.lang.Specification

import static org.assertj.core.api.Assertions.assertThat

@BlitzDataMongoTest(version = "5", isNoTableScan = true)
class BlitzDataMongoTestSpec extends Specification {

    @Autowired
    private ApplicationContext applicationContext

    def "Spring Environment has a property for MongoDB URI"() {
        expect:
        applicationContext.environment.containsProperty "spring.data.mongodb.uri"
    }


    def "verify MongoDb is up and running"() {
        given:
        def mongoClient = MongoClients.create(applicationContext.environment.getProperty("spring.data.mongodb.uri"))
        def database = mongoClient.getDatabase("test")
        def ping = new BsonDocument("ping", new BsonString("1"))

        when:
        def command = StepVerifier.create database.runCommand(ping)

        then:
        command.assertNext { assertThat(it).containsKey("ok") }
                .verifyComplete()
    }

    def "verify no other Spring Component is loaded"() {
        when:
        applicationContext.getBean DummySpringComponent

        then:
        thrown NoSuchBeanDefinitionException
    }
}