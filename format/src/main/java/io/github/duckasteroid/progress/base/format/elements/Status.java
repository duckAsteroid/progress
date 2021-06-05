package io.github.duckasteroid.progress.base.format.elements;

import io.github.duckasteroid.progress.ProgressMonitor;

/**
 * Adds the monitor status message to the output.
 */
public class Status implements FormatElement {
  @Override
  public void appendTo(StringBuilder sb, ProgressMonitor monitor) {
    if (hasContent(monitor)) {
      sb.append(Utils.sanitize(monitor.getStatus()));
    }
  }

  @Override
  public boolean hasContent(ProgressMonitor monitor) {
    if (monitor == null) {
      return false;
    }

    String status = monitor.getStatus();
    return Utils.notEmpty(status);
  }
}
