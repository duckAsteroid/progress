progress
========

A Java library for reporting progress - with console implementationa and bindings for various UI tookits.

If you are passed a `ProgressMonitor` instance into your code - it is very straightforward to use. Here is the most basic 
example:

```java
public void someMethod(ProgressMonitor monitor) {

  monitor.begin(100);
  
  try {
    for(int i=0; i < 10; i++) {
      monitor.notify("Starting " + i +" of 10");
      // do something that takes time
      monitor.worked(10);
    }
  }
  catch(IOException e) {
    e.printStackTrace();
  }
  finally {
    monitor.done();
  }
}
```

Whats most important is that this brings no other dependencies into the client code - however the underlying `ProgressMonitor` instance may be implemented (and mapped through to corresponding monitor classes) for Console, Swing, Eclipse ...
