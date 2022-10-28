package dev.blitzcraft.blitzcontainers.mongo

import com.mongodb.reactivestreams.client.MongoClients
import dev.blitzcraft.blitzcontainers.BlitzBootTest
import org.bson.BsonDocument
import org.bson.BsonInt64
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import reactor.test.StepVerifier
import spock.lang.Specification

import static org.assertj.core.api.Assertions.assertThat

@BlitzBootTest
@Mongo(version = '5')
class MongoSpec extends Specification {

    @Autowired
    private Environment environment

    def "Spring Environment has a property for MongoDB URI"() {
        expect:
        environment.containsProperty "spring.data.mongodb.uri"
    }

    def "MongoDb is up and running"() {
        given:
        def mongoDbUri = environment.getProperty("spring.data.mongodb.uri")
        def mongoClient = MongoClients.create(mongoDbUri)
        def database = mongoClient.getDatabase("test");

        when:
        def command = StepVerifier.create(database.runCommand(new BsonDocument("ping", new BsonInt64(1))))

        then:
        command.consumeNextWith { assertThat(it.containsKey("ok")).isTrue() }
                .verifyComplete()
    }
}