package io.github.duckasteroid.progress.base.event;

/**
 * An interface for objects that listen to a tree of progress monitors and get notified when they change work,
 * status or are done
 */
public interface ProgressMonitorListener {
    /**
     * Called when the supplied progress monitor has an update
     * @param event The event object
     */
    default void logUpdate(ProgressMonitorEvent event) {};
}
