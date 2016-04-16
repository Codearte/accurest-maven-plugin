[![Build Status](https://travis-ci.org/Codearte/accurest-maven-plugin.svg?branch=master)](https://travis-ci.org/Codearte/accurest-maven-plugin) [![GitHub issues](https://img.shields.io/github/issues/Codearte/accurest/maven.svg)](https://github.com/Codearte/accurest/labels/maven) [![Maven Central](https://img.shields.io/maven-central/v/io.codearte.accurest/accurest-maven-plugin.svg)](https://maven-badges.herokuapp.com/maven-central/io.codearte.accurest/accurest-maven-plugin)

Accurate REST Maven Plugin
====

Converting [Accurest](https://github.com/Codearte/accurest/wiki/1.-Introduction) GroovyDSL into WireMock stub mappings:

    mvn io.codearte.accurest:accurest-maven-plugin:convert
    
or shortly <sup>*</sup>

    mvn accurest:convert
    
For more information please go to the [Accurest Wiki](https://github.com/Codearte/accurest/wiki/2.2-Maven-Project) or [Plugin Documentation Site](http://codearte.github.io/accurest-maven-plugin/plugin-info.html).
  

    

Accurest Stub Runner
---

Run stubs mappings from current directory:

    mvn io.codearte.accurest:accurest-maven-plugin:run
    
or shortly <sup>*</sup>

    mvn accurest:run

Running stubs from repository:

    mvn accurest:run -Dstubs="org.springframework:gs-rest-service"  

where `org.springframework:gs-rest-service` is artifact with `stubs` classifier contains wiremock mappings.

---

<sup>*</sup> Additional configuration inside `~/.m2/settings.xml` is required.

```xml
<settings>
  ...
  <pluginGroups>
    <pluginGroup>io.codearte.accurest</pluginGroup>
  </pluginGroups>
  ...
</settings>

```

