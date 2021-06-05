package io.github.duckasteroid.progress.base.event;

import io.github.duckasteroid.progress.ProgressMonitor;
import io.github.duckasteroid.progress.base.BaseProgressMonitor;
import java.util.Objects;

/**
 * A simple event class to transfer progress from a monitor to a listener.
 */
public class ProgressMonitorEvent {
  /**
   * the monitor (possibly a child of the root) that caused the update.
   */
  private final ProgressMonitor source;
  /**
   * the root monitor that generated the event.
   */
  private final BaseProgressMonitor root;
  /**
   * the type of update that happened.
   */
  private final ProgressUpdateType type;

  /**
   * Create a new event.
   * @param root the root monitor of the event
   * @param source the source monitor of the event (may be same as root, or descendant)
   * @param type the type of event
   */
  public ProgressMonitorEvent(BaseProgressMonitor root, ProgressMonitor source,
                              ProgressUpdateType type) {
    this.root = root;
    if (source == null) {
      throw new IllegalArgumentException("Source cannot be null");
    }
    this.source = source;
    this.type = type;
  }

  public BaseProgressMonitor getRoot() {
    return root;
  }

  public ProgressMonitor getSource() {
    return source;
  }

  public ProgressUpdateType getType() {
    return type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProgressMonitorEvent that = (ProgressMonitorEvent) o;
    return Objects.equals(source, that.source);
  }

  @Override
  public int hashCode() {
    return Objects.hash(source);
  }

  @Override
  public String toString() {
    return "ProgressMonitorEvent{"
      + "root=" + root
      + ", source=" + source
      + ", type=" + type
      + "}";
  }
}
