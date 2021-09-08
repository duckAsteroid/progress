progress
========
[![Java CI with Gradle](https://github.com/duckAsteroid/progress/actions/workflows/gradle.yml/badge.svg?branch=master)](https://github.com/duckAsteroid/progress/actions/workflows/gradle.yml)
[![Known Vulnerabilities](https://snyk.io//test/github/duckAsteroid/progress/badge.svg?targetFile=build.gradle)](https://snyk.io//test/github/duckAsteroid/progress?targetFile=build.gradle)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com.asteroid.duck.progress%3Aprogress&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.asteroid.duck.progress%3Aprogress)

![Example console progress](example/console.gif?raw=true "Console GIF")

An implementation agnostic Java library for reporting progress - with bindings for various logging and UI toolkits.

Think of it like [SLF4J](https://www.slf4j.org/) but for progress reporting, rather than logging.

If you are passed a `ProgressMonitor` instance into your code - it is very straightforward to use. Here is the most basic 
example:

```java
public void someMethod(ProgressMonitor monitor) {
  monitor.setSize(100);
  monitor.setStatus("Starting to do some stuff");
  
  try {
    for(int i=0; i < 10; i++) {
      monitor.setStatus("Doing stuff " + i +" of 10");
      // do something sensible that takes time
      Thread.sleep(1000);
      monitor.worked(10);
      // Note we might also check if the monitor is cancelled?
      // if(monitor.isCancelled()) { break; }
    }
  }
  catch(InterruptedException e) {
    // could be any exception
    e.printStackTrace();
  }
  finally {
    // regardless what state of progress was when exception happened
    // we are now done!
    monitor.done();
  }
}
```

Whats most important is that this brings no other dependencies into the client code - however the underlying `ProgressMonitor` instance may be implemented (and mapped through to corresponding monitor classes) for Console, Swing, Eclipse ...

Getting Started
===============

To use the library to report on progress (regardless of where it reports to) you need to add the 
dependency for the pure API as follows:
```groovy
dependencies {
    implementation 'com.asteroid.duck.progress:api:1.1.0'
}
```

Applications that wish to render that progress would need to use one of the bindings for different
destinations:
* Console/terminal output (e.g. `System.out`)
* SLF4J Loggers messages in some form
* Java Logging framework  
* Swing progress reporting

More will be added in future.
