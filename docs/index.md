---
title: Introduction
layout: home
nav_order: 1
---
## Introduction

Do you know how to test [Spring Boot](https://spring.io/projects/spring-boot) applications with [TestContainers](https://www.testcontainers.org/)?
If so, you're probably aware of what an amazing tool it is, especially when you only need to write a few tests.
However, as you start writing more tests, you may find yourself faced with the challenge of avoiding repetitive code, which becomes way more of a problem, when working on multiple projects simultaneously in a microservices-oriented world.

But fear not! We have the solution for you.

**Introducing the `blitz-containers` library.** 
With this library, spinning up a Docker test container is as simple as adding a single annotation.
Literally.
No configuration files.

The primary goals of the `blitz-containers` library are:

1. Eliminate all the boilerplate code, enabling developers to write Spring integration tests that effortlessly spin up external services in Docker using just a single line of code.
2. No need to maintain any configuration outside the test class.
3. Provide easy configuration options to closely replicate production environments when testing with these external services.
4. Manage multiple integration tests efficiently by implementing a logic for reusing Docker containers, all while allowing parallel execution of these resource-intensive tests.
5. Facilitate the integration of multiple external services within a single test, making complex scenarios (such as end-to-end tests) easier to handle.

With the `blitz-containers` library, you can streamline your testing process and achieve reliable and efficient integration testing for your Spring Boot applications.

## Requirements

| Dependency    | Version  |
|---------------|----------|
| Java          | 17+      |
| Spring Boot   | 2.+, 3.+ |
| Docker client | *        |
