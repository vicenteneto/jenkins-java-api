# Jenkins Java API Wrapper

[![GitHub Release](https://img.shields.io/github/release/vicenteneto/jenkins-java-api.svg)](https://github.com/vicenteneto/jenkins-java-api/releases)
[![Build Status](https://img.shields.io/travis/vicenteneto/jenkins-java-api/master.svg)](https://travis-ci.org/vicenteneto/jenkins-java-api)

A wrapper for the Jenkins API written in Java.

## Usage

To use it in your Maven build add:
```xml
  <repositories>
    <repository>
      <id>jitpack.io</id>
      <url>https://jitpack.io</url>
    </repository>
  </repositories>
```

and the dependency:

```xml
  <dependencies>
    <dependency>
      <groupId>com.github.vicenteneto</groupId>
      <artifactId>jenkins-java-api</artifactId>
      <version>1.11.19</version>
    </dependency>
  </dependencies>
```

## Examples

### Jenkins Server
```java
JenkinsServer jenkinsServer = new JenkinsServer(new URI("http://0.0.0.0/jenkins"));

// If your Jenkins server uses any kind of security
JenkinsServer jenkinsServer = new JenkinsServer(new URI("http://0.0.0.0/jenkins"), "username", "password");
```

### Get Version
```java
String version = jenkinsServer.getVersion();
```

### Get a ListView by a given name
```java
ListView listView = jenkinsServer.getViewByName("viewName");
```

### Create a ListView
```java
jenkinsServer.createView("viewName");

// Create a Jenkins view with name and description
jenkinsServer.createView("viewName", "viewDescription");
```
