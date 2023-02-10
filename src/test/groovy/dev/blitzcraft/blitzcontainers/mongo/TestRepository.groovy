package dev.blitzcraft.blitzcontainers.mongo

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface TestRepository extends ReactiveMongoRepository<TestDocument, ObjectId>{}