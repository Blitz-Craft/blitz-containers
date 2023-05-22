---
title: Quick start
nav_order: 2
layout: default
---

## Adding the lib into your project

{: .tip}
To find the latest version take a look on our [Releases](https://github.com/Blitz-Craft/blitz-containers/releases)

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

1. MongoDB //todo link on page
2. Google PubSub

{: .tip}
Please feel free to vote on the issue or create a new one if you want to see a new container supported.
