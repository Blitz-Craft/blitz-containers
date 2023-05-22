---
title: MongoDB features
parent: Features overview
nav_order: 1
layout: default
---

{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

### `@Mongo`

This annotation, when added with `@SpringBootTest` or `@DataMongoTest`, allows you to start a configurable MongoDB server.

{: .warning}
> It is only suited for the projects using [Spring Data MongoDB](https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/).
>
> It works well, however, with both reactive and classic flavors.

### Tag

```java
@Mongo(tag = "5.0")
@SpringBootTest
public class MyMongoTest { ... }
```

This is the only **mandatory** parameter.
It is possible to pass any valid docker tag of the official MongoDB image.

{: .tip}
> While you have the option to use `tag = "latest"`, we discourage its use as the default value.
>
> It is important to maintain version alignment between your testing and production environments.
>
> Updating images without explicit intention can lead to unexpected failures.
> Therefore, we recommend explicitly specifying the desired image versions to ensure consistency and stability.

{: .warning}
> Changing the tag will trigger the download of a new Docker image to your local repository.
> The download time may vary depending on your Internet bandwidth.

### No table scan

```java
@Mongo(tag = "6", isNoTableScan = true)
@DataMongoTest
public class MyRepositoryTest { ... }
```

The parameter, having `false` by default, can activate MongoDB's `notablescan` feature.
Activating this flag will result in any MongoDB query that lacks an index on the collection to crash with an exception.
This helps to ensure that all your custom queries are indeed backed by some index.
If you need more details about this mode, [here is the official documentation](https://www.mongodb.com/docs/manual/reference/parameters/#mongodb-parameter-param.notablescan).

{: .tip}
It makes sense to activate this feature while doing sliced integration tests via `@DataMongoTest`, because normally your higher-level tests focus on other things.

{: .warning}
As of now, notablescan feature is known to be incompatible with MongoDB Transactions.

### Spring properties automatically set when using the annotation

| Spring property           | Value                                                      |
|---------------------------|------------------------------------------------------------|
| `spring.data.mongodb.uri` | defined on runtime, an URI pointing to the Docker instance |
