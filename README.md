progress
========

A Java library for reporting progress - with console implementationa and bindings for various UI tookits.

If you are passed a `ProgressMonitor` instance into your code - it is very straightforward to use. Here is the most basic 
example:

```java
public void someMethod(ProgressMonitor monitor) {
  monitor.begin(100);
  monitor.notify("Starting to do some stuff");
  
  try {
    for(int i=0; i < 10; i++) {
      monitor.notify("Doing stuff " + i +" of 10");
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
