---
title: MariaDB features
parent: Features overview
nav_order: 4
layout: default
---

{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

### `@MariaDb`

The `@MariaDb` annotation allows you to start a MariaDB server and automatically set up a connection for JDBC or
R2DBC drivers when used with `@SpringBootTest` or Spring Data Slice test annotations.

### Tag

```java
@MariaDb(tag = "10")
@SpringBootTest
public class MyMariaDbTest { 
   ...
}
```

This is the only **mandatory** parameter.
It is possible to pass any valid docker tag of the official MariaDB image.

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

### JDBC

`@MariaDb` works seamlessly with `spring-jdbc`, `spring-data-jdbc`, and `spring-data-jpa`.
It can be used in conjunction with the respective slice test annotations: `@JdbcTest`, `@DataJdbcTest`,
and `@DataJpaTest`

{: .tip}
> By default `@JdbcTest`,`@DataJdbcTest`, and `@DataJpaTest` use an in-memory database.
> However, this feature is automatically disabled when you add the `@MariaDb` annotation.

#### Spring properties automatically set for JDBC

| Spring property                | Value              |
|--------------------------------|--------------------|
| `spring.datasource.url`        | defined on runtime |
| `spring.datasource.username`   | defined on runtime |
| `spring.datasource.password`   | defined on runtime |
| `spring.test.database.replace` | "NONE"             |

### R2BC

If you are using an R2DBC driver, `@MariaDb` will detect it and configure the connection, ensuring seamless
integration.
Just like JDBC, `@MariaDb` can be used for slice test with `@DataR2dbcTest` annotation.

{: .tip}
> The presence of the `org.mariadb.jdbc:mariadb-java-client` driver in the classpath is checked to determine if the R2DBC
> connection needs to be configured

#### Spring properties automatically set for R2DBC

| Spring property         | Value              |
|-------------------------|--------------------|
| `spring.r2dbc.url`      | defined on runtime |
| `spring.r2dbc.username` | defined on runtime |
| `spring.r2dbc.password` | defined on runtime |
