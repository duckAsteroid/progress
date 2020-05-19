package org.duck.asteroid.progress.base.event;

import org.duck.asteroid.progress.ProgressMonitor;
import org.duck.asteroid.progress.base.BaseProgressMonitor;

import java.util.Objects;

/**
 * A simple event class
 */
public class ProgressMonitorEvent {
    /** the monitor that caused the update */
    private final ProgressMonitor source;
    /** the root monitor that generated the event */
    private final BaseProgressMonitor root;
    /** the type of update that happened */
    private final ProgressUpdateType type;

    public ProgressMonitorEvent(BaseProgressMonitor root, ProgressMonitor source, ProgressUpdateType type) {
        this.root = root;
        if (source == null) throw new IllegalArgumentException("Source cannot be null");
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProgressMonitorEvent that = (ProgressMonitorEvent) o;
        return Objects.equals(source, that.source);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source);
    }

    @Override
    public String toString() {
        return "ProgressMonitorEvent{" +
                "root=" + root +
                ", source=" + source +
                ", type=" + type +
                '}';
    }
}
