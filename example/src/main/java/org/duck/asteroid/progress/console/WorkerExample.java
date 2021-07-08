package org.duck.asteroid.progress.console;

import io.github.duckasteroid.progress.ProgressMonitor;

/**
 * This is an example of a class that only uses the pure progress reporting API
 */
public class WorkerExample {
  private final long sleep;
  private final int repeat;

  public WorkerExample(long sleep, int repeat) {
    this.sleep = sleep;
    this.repeat = repeat;
  }

  /**
   * This is the main work method - it does one unit of work repeat times
   * and waits sleep between each.
   * @param monitor a monitor to report work via
   */
  public void doSomething(ProgressMonitor monitor) {
    // set the size of the monitor to the total amount of work
    monitor.setSize(repeat);
    for (int i=0; i < repeat; i++) {
      // check if we have been cancelled via the monitor
      if (monitor.isCancelled()) {
        break;
      }
      // report work with a status message
      monitor.worked(1, "Did a bit of work ["+i+"]");
      // go to sleep...
      try {
        Thread.sleep(sleep);
      } catch (InterruptedException e) {
        break;
      }
    }

    // ensure monitor is done
    monitor.done();
  }
}
