package org.duck.asteroid.progress.console;

import io.github.duckasteroid.progress.ProgressMonitor;
import io.github.duckasteroid.progress.ProgressMonitorFactory;
import java.util.Date;
import java.util.Random;

/**
 * An example using the console progress.
 */
public class ConsoleExample {

  private static Random rnd = new Random();

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
    ProgressMonitor monitor = ProgressMonitorFactory.newMonitor(ConsoleExample.class.getName(), 2);
    for (int i = 0; i < 2; i++) {
      // create a sub task for each of the 2 units of work
      ProgressMonitor subTask = monitor.newSubTask("L1:" + i, 1);
      // 3 units of work per sub task
      subTask.setSize(3);
      for (int j = 0; j < 3; j++) {
        // create a monitor for each sub task...
        ProgressMonitor subSubTask = subTask.newSubTask("L2:" + i + "." + j, 1);
        // pass over to client to actually "do" the work...
        doWork(4, 1, 250, subSubTask);
      }
    }
    // we are done (whatever happened)
    monitor.done();
  }

  /**
   * Example of client that does some work and reports via a progress.
   *
   * @param work the amount of work to perform
   * @param stepSize the amount of work in each step
   * @param sleep the millis to sleep each step
   * @param monitor the monitor to update
   */
  public static void doWork(int work, int stepSize, int sleep, ProgressMonitor monitor) {
    monitor.setSize(work);
    for (int j = 0; j < work; j += stepSize) {
      try {
        Thread.sleep(sleep);
      } catch (InterruptedException e) {
        break;
      }
      if (rnd.nextBoolean()) {
        monitor.setStatus("Just a status message, not progress");
      }
      monitor.worked(stepSize, new Date().toString());

    }
    monitor.done();
  }


}