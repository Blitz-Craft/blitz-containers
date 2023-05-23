---
title: Quick start
nav_order: 2
layout: default
---

## Adding the lib into your project

{: .tip}
>Here's our actual latest version:
> 
>![GitHub release (latest by date)](https://img.shields.io/github/v/release/Blitz-Craft/blitz-containers?label=Latest%20version&style=flat&color=gr)

### Gradle:
```groovy
dependencies {
  testImplementation "dev.blitzcraft:blitz-containers:$someVersion"
}
```
### Maven:

```xml
<dependencies>
    <dependency>
      <groupId>dev.blitzcraft</groupId>
      <artifactId>blitz-containers</artifactId>
      <version>${some_version}</version>
      <scope>test</scope>
    </dependency>
</dependencies>
``` 

## Supported Containers

1. [MongoDB](../features/mongodb)
2. [Google PubSub](../features/pubsub)

{: .tip}
Please feel free to vote on the issue or create a new one if you want to see a new container supported.
