package org.duck.asteroid.progress.console;

import io.github.duckasteroid.progress.ProgressMonitor;
import io.github.duckasteroid.progress.ProgressMonitorFactory;

/**
 * An example using the console progress.
 */
public class BasicExample {

  /**
   * This is an example of a client application that wishes to provide a progress monitor to other
   * code.
   * This example is not concerned with the details of how that monitor is configured so obtains an
   * instance from {@link ProgressMonitorFactory}
   *
   * @param args Command line arguments (none required)
   */
  public static void main(String[] args) {
    // obtain a monitor instance from the factory for this class and size of job = 2
    ProgressMonitor monitor = ProgressMonitorFactory.newMonitor(BasicExample.class.getName(), 2);

    WorkerExample worker1 = new WorkerExample(50, 50);
    worker1.doSomething(monitor.newSubTask("First worker"));

    WorkerExample worker2 = new WorkerExample(100, 25);
    worker2.doSomething(monitor.newSubTask("Second worker"));

    monitor.done();
  }
}