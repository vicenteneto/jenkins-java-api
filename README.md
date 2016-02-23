# Jenkins Java API Wrapper

[![GitHub Release](https://img.shields.io/github/release/vicenteneto/jenkins-java-api.svg)](https://github.com/vicenteneto/jenkins-java-api/latest)
[![Build Status](https://travis-ci.org/vicenteneto/jenkins-java-api.svg?branch=master)](https://travis-ci.org/vicenteneto/jenkins-java-api)
[![License](http://img.shields.io/:license-mit-blue.svg)](https://github.com/vicenteneto/jenkins-java-api/blob/master/LICENSE)

This project is a Java library for communicating with [Jenkins](https://github.com/jenkinsci/jenkins/). Jenkins is a cross-platform, continuous integration and continuous delivery application that increases your productivity ([see more](https://wiki.jenkins-ci.org/display/JENKINS/Meet+Jenkins)).

## Table of contents

* [Getting started](#getting-started)
* [Contributing](#contributing)
* [Creator](#creator)
* [Copyright and license](#copyright-and-license)

## Getting started

#### Maven
To use it in your Maven build add it to your **pom.xml** file:
```xml
  <repositories>
    <repository>
      <id>jitpack.io</id>
      <url>https://jitpack.io</url>
    </repository>
  </repositories>
```
and:
```xml
  <dependencies>
    <dependency>
      <groupId>com.github.vicenteneto</groupId>
      <artifactId>jenkins-java-api</artifactId>
      <version>v1.15.23</version>
    </dependency>
  </dependencies>
```

#### Gradle
To use it in your Gradle build add it to your **gradle.build** file:
```gradle
repositories {
    maven { url "https://jitpack.io" }
}
```
and:
```gradle
dependencies {
    compile 'com.github.vicenteneto:jenkins-java-api:v1.15.23'
}
```

[Check out our examples](https://github.com/vicenteneto/jenkins-java-api/blob/master/docs/examples.md)

Take note that our master branch is our active, unstable development branch and that if you're looking to download a stable copy of the repo, check the [tagged downloads](https://github.com/vicenteneto/jenkins-java-api/tags).

## Contributing

Have a bug or a feature request? [Please, open a GitHub issue](https://github.com/vicenteneto/jenkins-java-api/issues/new).

## Creator

**Vicente Neto**

* <https://github.com/vicenteneto>

## Copyright and license

Copyright 2015-, Vicente Neto. This project is licensed under the [MIT License](https://github.com/vicenteneto/jenkins-java-api/blob/master/LICENSE).
