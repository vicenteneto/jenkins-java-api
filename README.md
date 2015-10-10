# Jenkins Java API Wrapper

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
      <version>0.0.1.8</version>
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
