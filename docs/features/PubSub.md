---
title: Google PubSub features
parent: Features
nav_order: 2
---
{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

### `@PubSub`
This annotation, when added with `@SpringBootTest`, allows you to start a GCP Emulator container with a PubSub.

{: .warning}
It is only suited for the projects using [Google Spring Cloud PubSub](https://cloud.google.com/pubsub/docs/spring).

### Tag
```java
@PubSub(tag = "emulators")
@SpringBootTest
public class MyPubSubTest { ... }
```
This is the only **mandatory** parameter.
It is possible to pass any valid docker tag of the `gcr.io/google.com/cloudsdktool/cloud-sdk` image.

{: .warning}
> Changing the tag will trigger the download of a new Docker image to your local repository.
> The download time may vary depending on your Internet bandwidth.

### Topics
PubSub, in contrast to Kafka, does not allow topics or subscriptions to be created on-the-fly.
This becomes an annoying problem for developers aiming to write integration tests, as the setup-cleanup code can quickly bypass a hundred lines. 
Once this laborious process is done, the prospect of repeating it is something to be avoided at all costs.

So, `blitz-containers` has a solution for you:
```kotlin
@PubSub(
    tag = "emulators",
    topics = [Topic("topic-to-test", [Subscription("my-subscription")])]
)
@SpringBootTest
class PubSubTest { ... }
```

As it could be seen in the example, `topics` is a property, expecting an array of `@Topic` child annotations.
Each `@Topic` has a `name`, and, optionally, `subscriptions`.
`subscriptions`, in turn, are also an array of a `@Subscription` annotations, each of them simply having a `name`.
There's not much more to say about it.

Everything is created, linked, and ready-to-test before your Spring test application is started.
So, in the given example, the PubSub will have a topic named `topic-to-test` with a subscription named `my-subscription`.

{: .tip}
> You should ensure that your application code is configured for the tests to be working with the same topic and subscription.

### Spring properties automatically set when using the annotation

| Spring property                         | Value                                                      |
|-----------------------------------------|------------------------------------------------------------|
| `spring.cloud.gcp.pubsub.emulator-host` | defined on runtime, an URI pointing to the Docker instance |
| `spring.cloud.gcp.pubsub.project-id`    | `blitzcontainers-pubsub`                                   |
