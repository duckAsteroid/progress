package org.duck.asteroid.progress.base.event;

/**
 * A factory for listeners that create new objects based on the name of the
 * progress created.
 * For example logging frameworks might use a special log destination for different
 * named progress monitors?
 */
public interface ProgressMonitorListenerFactory {
    /**
     * Create a {@link ProgressMonitorListener} for the named progress
     * @param name the name of the progress
     * @return a {@link ProgressMonitorListener} instance for the name
     */
    ProgressMonitorListener createProgressMonitorListener(String name);
}
