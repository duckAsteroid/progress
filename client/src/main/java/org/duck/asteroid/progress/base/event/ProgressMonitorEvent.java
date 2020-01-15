package org.duck.asteroid.progress.base.event;

import org.duck.asteroid.progress.ProgressMonitor;

import java.util.Objects;

/**
 * A simple event class
 */
public class ProgressMonitorEvent {
    /** the monitor that caused the update */
    private final ProgressMonitor source;
    /** the type of update that happened */
    private final ProgressUpdateType type;

    public ProgressMonitorEvent(ProgressMonitor source, ProgressUpdateType type) {
        if (source == null) throw new IllegalArgumentException("Source cannot be null");
        this.source = source;
        this.type = type;
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
                "source=" + source +
                ", type=" + type +
                '}';
    }
}
