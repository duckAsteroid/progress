package io.github.duckasteroid.progress.swing;

import io.github.duckasteroid.progress.ProgressMonitor;
import io.github.duckasteroid.progress.base.BaseProgressMonitor;
import io.github.duckasteroid.progress.base.event.ProgressMonitorEvent;
import io.github.duckasteroid.progress.base.event.ProgressMonitorListener;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JProgressBar;

/**
 * A progress bar model - that can be used with a {@link JProgressBar}.
 */
public class JProgressBarModel extends DefaultBoundedRangeModel implements ProgressMonitorListener {
  private static final long serialVersionUID = -1659525008252981826L;
  private transient String status;

  /**
   * Create a progress monitor to wrap a Swing progress bar.
   *
   * @param bar       the swing progress bar to wrap
   * @param taskName  the name of the task
   * @param totalWork the total work to do in the monitor
   * @return a progress monitor instance that updates the swing progress bar
   */
  public static ProgressMonitor newMonitor(JProgressBar bar, String taskName, int totalWork) {
    BaseProgressMonitor monitor = new BaseProgressMonitor(taskName, totalWork);
    JProgressBarModel model = new JProgressBarModel();
    monitor.addProgressMonitorListener(model);
    bar.setModel(model);
    return monitor;
  }

  @Override
  public void logUpdate(ProgressMonitorEvent event) {
    setMaximum((int) event.getRoot().getSize());
    setValue((int) event.getRoot().getWorkDone());
    this.status = event.getSource().getStatus();
  }

  public String getStatus() {
    return status;
  }
}
