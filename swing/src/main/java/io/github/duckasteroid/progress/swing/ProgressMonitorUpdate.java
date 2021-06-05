package io.github.duckasteroid.progress.swing;


import io.github.duckasteroid.progress.ProgressMonitor;
import io.github.duckasteroid.progress.base.BaseProgressMonitor;
import io.github.duckasteroid.progress.base.event.ProgressMonitorEvent;
import io.github.duckasteroid.progress.base.event.ProgressMonitorListener;
import java.awt.Component;

/**
 * An AWT progress monitor.
 */
public class ProgressMonitorUpdate implements ProgressMonitorListener {
  private final transient javax.swing.ProgressMonitor swing;
  private final transient BaseProgressMonitor progress;

  /**
   * Create a new AWT monitor.
   * @param parent the parent component
   * @param name the name of the monitor
   * @param totalWork the amount of work
   */
  public ProgressMonitorUpdate(Component parent, String name, int totalWork) {
    this.swing = new javax.swing.ProgressMonitor(parent, name, "", 0, totalWork);
    this.progress = new BaseProgressMonitor(name, totalWork);
    this.progress.addProgressMonitorListener(this);
  }

  public javax.swing.ProgressMonitor getSwingMonitor() {
    return this.swing;
  }

  public ProgressMonitor getProgressMonitor() {
    return progress;
  }

  @Override
  public void logUpdate(ProgressMonitorEvent event) {
    swing.setMaximum((int) event.getRoot().getSize());
    swing.setProgress((int) event.getRoot().getWorkDone());
    swing.setNote(event.getSource().getStatus());
  }
}
