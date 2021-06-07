package io.github.duckasteroid.progress.slf4j;

import io.github.duckasteroid.progress.ProgressMonitor;

public class StandardProgressSequence implements Runnable {
  public static final int EXPECTED_NUM_EVENTS = 40;
  private final ProgressMonitor monitor;

  public StandardProgressSequence(ProgressMonitor monitor) {
    this.monitor = monitor;
  }

  @Override
  public void run() {
    monitor.setSize(3);
    for (int i = 0; i < 3; i++) {
      ProgressMonitor subTask = monitor.newSubTask("Task " + i, 1);
      doSubTask(subTask);
    }
    monitor.done();
  }

  private void doSubTask(ProgressMonitor monitor) {
    monitor.setSize(10);
    monitor.setStatus("Starting");
    for (int i = 0; i < 10; i++) {
      if (i % 3 == 0) {
        monitor.worked(1, "Divisible by three");
      }
      else {
        monitor.worked(1, "Doing stuff");
      }
    }
    monitor.done();
  }
}
