# Jenkins Java API Wrapper

[![GitHub Release](https://img.shields.io/github/release/vicenteneto/jenkins-java-api.svg)](https://github.com/vicenteneto/jenkins-java-api/releases)
[![Build Status](https://img.shields.io/travis/vicenteneto/jenkins-java-api/master.svg)](https://travis-ci.org/vicenteneto/jenkins-java-api)

This project is a Java library for communicating with [Jenkins](https://github.com/jenkinsci/jenkins/). Jenkins is a cross-platform, continuous integration and continuous delivery application that increases your productivity ([see more](https://wiki.jenkins-ci.org/display/JENKINS/Meet+Jenkins)).

## Table of contents

* [Contributing](#contributing)
* [Usage](#usage)
* [Examples](#examples)
* [License](#license)

## Contributing

Please file a GitHub issue to [report a bug](https://github.com/vicenteneto/jenkins-java-api/issues).

## Usage

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
      <version>v1.13.11</version>
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
    compile 'com.github.vicenteneto:jenkins-java-api:v1.12.6'
}
```

## Examples

#### Jenkins Server
```java
JenkinsServer jenkinsServer = new JenkinsServer(new URI("http://0.0.0.0/jenkins"));

// If your Jenkins server uses any kind of security
JenkinsServer jenkinsServer = new JenkinsServer(new URI("http://0.0.0.0/jenkins"), "username", "password");
```

#### Get Version
```java
String version = jenkinsServer.getVersion();
```

#### Set Jenkins Security Realm
This project supports 2 types of security: **Hudson private database** and **LDAP**.
```java
HudsonPrivateSecurityRealm hudsonSecurity = new HudsonPrivateSecurityRealm(true);
jenkinsServer.setSecurityRealm(hudsonSecurity);
```

#### Set Jenkins Authorization Strategy
This project supports 3 types of authorization strategy: **Unsecured**, **Full control once logged in** and **Project matrix**.
```javainstallPluginByName
FullControlOnceLoggedInAuthorizationStrategy loggedInAuthorization = new FullControlOnceLoggedInAuthorizationStrategy();
jenkinsServer.setSecurityRealm(loggedInAuthorization);
```

#### Set Jenkins Slave Agent Port
Use **0** to indicate random available TCP port and **-1** to disable this service.
```java
jenkinsServer.setSlaveAgentPort(789);
```

#### Get Plugin by name
[Jenkins Plugins](https://wiki.jenkins-ci.org/display/JENKINS/Plugins)
```java
Plugin plugin = jenkinsServer.getPluginByName("junit");
```

#### Check if plugin exists
```java
boolean pluginExists = jenkinsServer.checkPluginExists("junit");
```

#### Install plugin by name
If **dynamic load** is true, the plugin will be dynamically loaded into this Jenkins. If false, the plugin will only take effect after the reboot.
```java
jenkinsServer.installPluginByName("junit", true);
```

#### Update all installed plugins
```java
jenkinsServer.updateAllInstalledPlugins();
```

#### Get a ListView by name
```java
ListView listView = jenkinsServer.getViewByName("viewName");
```

#### Check if a view exists
```java
boolean viewExists = jenkinsServer.checkViewExists("viewName");
```

#### Create a ListView
```java
jenkinsServer.createView("viewName");

// Create a Jenkins view with name and description
jenkinsServer.createView("viewName", "viewDescription");
```

#### Delete a View
```java
jenkinsServer.deleteView("viewName");
```

#### Get a Job by name
```java
Job job = jenkinsServer.getJobByName("jobName");
```

#### Check if a job exists
```java
boolean jobExists = jenkinsServer.checkJobExists("jobName");
```

#### Create a Job
```java
jenkinsServer.createJob("jobName");
```

#### Delete a Job
```java
jenkinsServer.deleteJob("jobName");
```

#### Add Job to View
```java
jenkinsServer.addJobToView("viewName", "jobName");
```

#### Execute Job
```java
int buildNumber = jenkinsServer.executeJob("jobName");
```

#### Add user to project matrix authorization table
```java
List<Permission> permissions = new LinkedList<Permission>();
permissions.add(Permission.HUDSON_ADMINISTER);
permissions.add(Permission.HUDSON_CONFIGURE_UPDATE_CENTER);

jenkinsServer.addUserToProjectMatrix("jobName", "username", permissions);
```

#### Remove user from project matrix authorization table
```java
jenkinsServer.removeUserFromProjectMatrix("jobName", "username");
```

#### Get Code Coverage report
```java
CoverageReport coverageReport = jenkinsServer.getCoverageReport("jobName", 1);
```

#### Get Code Coverage Element report
```java
CoverageElement coverageReportElement = jenkinsServer.getCoverageReportElement("jobName", 1, CoverageType.CONDITIONALS);
```

#### Get Test Results report
```java
TestResultsReport testResultsReport = jenkinsServer.TestResultsReport("jobName", 1);
```

#### Get Static Analysis report
```java
StaticAnalysisReport staticAnalysisReport = jenkinsServer.getStaticAnalysisReport("jobName", 1);
```

## License

jenkins-java-api is licensed under the [MIT License](http://opensource.org/licenses/MIT).