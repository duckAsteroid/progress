progress
========
![build status](https://travis-ci.org/duckAsteroid/progress.svg?branch=master)
[![Known Vulnerabilities](https://snyk.io//test/github/duckAsteroid/progress/badge.svg?targetFile=build.gradle)](https://snyk.io//test/github/duckAsteroid/progress?targetFile=build.gradle)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com.asteroid.duck.progress%3Aprogress&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.asteroid.duck.progress%3Aprogress)

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
