---
title: Features
nav_order: 3
has_children: true
has_toc: false
layout: default
---
{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

## How to configure the library?
Sit back and relax, there is literally no configuration needed. Just add it as a dependency and start testing.

## How many containers are too many?
Before diving into the annotation features of `blitz-containers`, it's essential to understand container caching. 
The `blitz-containers` library aims to minimize the time-consuming process of starting and stopping Docker images, optimizing container usage.
When the Spring test framework initiates the next test, the library analyzes whether a Docker container with the same configuration has already been started for any previous tests. 
If such a container exists, it will be reused. 
In other words, if the `blitz-containers` annotation with identical properties is present in all tests, they will run against the same Docker instance.

{: .tip}
> Remember to ensure that your tests are independent of each other!
> 
> Avoid potential failures due to lingering data from a previous test execution! 

## Combining multiple containers

For complex test setups, such as end-to-end testing, it is often desirable to have multiple containers of different types simultaneously.
`blitz-containers` allows to mix up together any combination of supported containers, without any additional effort, for example:
```java
@Mongo(tag = "6")
@PubSub(tag = "emulators")
@SpringBootTest
public class MyHugeTest { ... }
```

## MongoDB support
[Check this dedicated documentation page for everything about MongoDb](mongodb)
## Google PubSub support
[Check this dedicated documentation page for everything about Google PubSub](pubsub)
