#RestNucleus Boilerplate


## Introduction:

RestNucleus Boilerplate is a Java stack of frameworks to easily create RESTful services on almost any datastore. Like [Dropwizard](http://dropwizard.codahale.com/) it is
> a little bit of opinionated glue code which bangs together a set of libraries which have historically not sucked.

The stack is different from Dropwizard, as it is composed of:

* [JSR 311](http://jcp.org/en/jsr/detail?id=311), an API for RESTful Web Services, implemented by [Jersey](https://jersey.java.net/), an framework for Java.
* [JDO 3.0](http://db.apache.org/jdo/releases/release-3.0.cgi), implemented by [Datanucleus](http://www.datanucleus.org), a flexibile API and datastore.

I found myself using these always same libraries to create RESTful services for different kind of project and pratforms. I have assembled this stack of libraries into a stub to be easily extended, tested locally and deployed to Tomcat, AWS Beanstalk, and GAE. Maybe someone might prefer this pure-java aproach to the many opinionated frameworks out there. 


## Compile and run:

1. [Install maven](http://maven.apache.org/download.cgi#Installation_Instructions)

2. Compile: 
   * `mvn clean install`


3. Start local development environment
   * `cd RestNucleusBoilerplate`
   * `mvn jetty:run -Denvironment=test`


4. Open your browser at [http://localhost:8080/](http://localhost:8080/)

## Maven Dependency:

```xml
<dependencies>
  ...
  <dependency>
    <artifactId>RestNucleusBoilerplate</artifactId>
    <groupId>org.restnucleus</groupId>
    <version>0.2.2</version>
  </dependency>
</dependencies>
```

## Maven Repository on Github:
<repositories>
    <repository>
        <id>RestNucleus-Boilerplate</id>
        <url>https://raw.github.com/johannbarbie/RestNucleus-Boilerplate/mvn-repo/</url>
        <snapshots>
            <enabled>false</enabled>
            <updatePolicy>always</updatePolicy>
        </snapshots>
    </repository>
</repositories>

## Help:

Reach out for me on twitter: @johba

## License:

Copyright 2014 Johann Barbie

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
