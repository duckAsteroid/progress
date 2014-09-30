package org.duck.asteroid.progress;
/**
 * A listener interface for being notified of changes/updates in a progress monitor
 */
public interface ProgressMonitorListener {
	/**
	 * An enumeration of event types this listener is notified of
	 */
	public enum EventType {
		BEGIN, WORK_DONE, COMPLETED, NOTIFICATION, NAME_CHANGED, CANCELLED
	}
	/**
	 * Called when a progress monitor has been updated
	 * @param event The type of event that caused the update
	 * @param source The source monitor that raised the event
	 */
	public void progressUpdated(EventType event, ProgressMonitor source);
}
