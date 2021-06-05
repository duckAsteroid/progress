package io.github.duckasteroid.progress.base.format.elements;

import io.github.duckasteroid.progress.ProgressMonitor;

/**
 * Adds the monitor taskname to the output.
 */
public class TaskName implements FormatElement {
  @Override
  public void appendTo(StringBuilder sb, ProgressMonitor monitor) {
    sb.append(Utils.sanitize(monitor.getTaskName()));
  }
}
