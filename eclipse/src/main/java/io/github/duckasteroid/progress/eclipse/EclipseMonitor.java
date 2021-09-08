package io.github.duckasteroid.progress.eclipse;

import io.github.duckasteroid.progress.ProgressMonitor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

public class EclipseMonitor implements ProgressMonitor {
  private final ProgressMonitor parent;
  private final SubMonitor monitor;
  private long size = 0;
  private AtomicLong workDone = new AtomicLong(0L);
  private String taskName = "";
  private String status = "";
  private String unit = "";

  public EclipseMonitor(IProgressMonitor monitor) {
    this(SubMonitor.convert(monitor), null);
  }

  public EclipseMonitor(SubMonitor monitor, ProgressMonitor parent) {
    this.monitor = monitor;
    this.parent = parent;
  }

  @Override
  public ProgressMonitor getParent() {
    return null;
  }

  @Override
  public List<ProgressMonitor> getContext() {
    ArrayList<ProgressMonitor> result = new ArrayList<>();
    ProgressMonitor pm = this;
    do {
      result.add(pm);
      pm = pm.getParent();
    } while (pm != null);
    return Collections.unmodifiableList(result);
  }

  @Override
  public String getTaskName() {
    return taskName;
  }

  @Override
  public String getStatus() {
    return status;
  }

  @Override
  public void setStatus(String status) {
    monitor.setTaskName(status);
  }

  @Override
  public String getUnit() {
    return unit;
  }

  @Override
  public void setUnit(String unit) {
    this.unit = unit;
  }

  /**
   * Computes the task name (including status if any).
   * @return the task name
   */
  public String taskName() {
    String result = taskName;
    if (status != null && status.length() > 0) {
      result += " - " + status;
    }
    if (unit != null && unit.length() > 0) {
      result += " (" + unit + ")";
    }
    return result;
  }

  @Override
  public long getSize() {
    return size;
  }

  @Override
  public void setSize(long size) {
    this.size = size;
    monitor.beginTask(taskName(), (int) size);
  }

  @Override
  public long getWorkDone() {
    return workDone.get();
  }

  @Override
  public long worked(long amount, String status) {
    setStatus(status);
    monitor.worked((int)amount);
    return workDone.addAndGet(amount);
  }

  @Override
  public long worked(long amount) {
    return worked(amount, "");
  }

  @Override
  public void done() {
    long work;
    do {
      work = workDone.get();
    } while (workDone.compareAndSet(work, size));
    monitor.done();
  }

  @Override
  public boolean isDone() {
    return workDone.get() == size;
  }

  @Override
  public void close() {
    done();
  }

  @Override
  public boolean isCancelled() {
    return monitor.isCanceled();
  }

  @Override
  public void setCancelled(boolean cancelled) {
    monitor.setCanceled(cancelled);
  }

  @Override
  public ProgressMonitor newSubTask(String name, long work) {

    return null;
  }

  @Override
  public ProgressMonitor newSubTask(String name) {
    return null;
  }

  @Override
  public double getFractionDone() {
    return workDone.doubleValue() / (double)size;
  }

  @Override
  public int compareTo(ProgressMonitor o) {
    return 0;
  }
}
