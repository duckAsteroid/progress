package org.duck.asteroid.progress;

import java.util.List;

/**
 * The interface to an object an application can use to report work on a task.
 */
public interface ProgressMonitor {	
	/**
	 * Begin this task and set the {{@link #getTotalWork() total amount work} that this task entails.
	 * 
	 * @param total the total work in this task 
	 */
	void begin(int total);
	/**
	 * The name of this task (if any)
	 * @return the task name
	 */
	String getTaskName();
	/**
	 * The last {@link #notify(String) notified} status
	 * @return the task status
	 */
	String getStatus();
	/**
	 * The total amount work that this task entails.
	 * The default (if not provided in {@link #begin(int, String)} is 100)
	 * @return The total work. A positive integer
	 */
	int getTotalWork();
	
	/**
	 * The work done so far (always less than or equal to {@link #getTotalWork()})
	 * @return The work done so far. A positive integer.
	 */
	int getWorkDone();
	
	/**
	 * Set the absolute work done so far. This can be less than the current {@link #getWorkDone()}.
	 * Attempting to set this value larger than {@link #getTotalWork()} will result 
	 * in {@link #getWorkDone()} == {@link #getTotalWork()}.
	 * Attempting to set this value to less than zero will result in {@link #getWorkDone()} == 0. 
	 * @param done The work done so far.
	 */
	void setWorkDone(int done);
	
	/**
	 * The remaining work to be done. Always less than {@link #getTotalWork()}.
	 * @return The work remaining
	 */
	int getWorkRemaining();
	
	/**
	 * Set the absolute work remaining. 
	 * Attempts to set the new remaining value more than the {@link #getWorkRemaining() current remaining value}, are ignored.
	 * Attempts to set the remaining work negative are ignored.
	 * Attempts to set the remaining work to be more than the {@link #getTotalWork() total work} are ignored.  
	 * @param remaining The new amount of work remaining.
	 */
	void setWorkRemaining(int remaining);
	
	/**
	 * Log an amount of work being done. 
	 * Negative work values, or work values that take the {@link #getWorkDone() work done} so far
	 * beyond the {{@link #getTotalWork() total work} are ignored. 
	 * @param work The work that has been done.
	 * @param status A new status message (or null to leave as is)
	 */
	void worked(int work, String status);
	
	/**
	 * Clients may call this to indicate all work on this task is complete.
	 * Equivalent to calling <code>progress.setWorkDone(progress.getWorkRemaining())</code>
	 */
	void done();
	
	/**
	 * Is the work reported complete (e.g. does {@link #getWorkDone()} == {@link #getTotalWork()})
	 * @return true if the work is complete
	 */
	boolean isWorkComplete();
	
	/**
	 * Returns the value of {@link #getWorkDone()}/{@link #getTotalWork()} as a double fraction.
	 * @return The fraction of work done
	 */
	double getFractionDone();
	
	/**
	 * Notify users (if possible) of the status of this task.
	 * @param status The task status notification message
	 */
	void notify(String status);
	
	/**
	 * Has the task being reported been cancelled.
	 * @return true if cancelled
	 */
	boolean isCancelled();
	/**
	 * Change the cancelled state of the task being monitored
	 * @param cancelled the new cancelled state
	 */
	void setCancelled(boolean cancelled);

	/**
	 * Add a sub task to this task. When completed this sub task will contribute the given amount of
	 * work to this task.
	 * @param work The amount of work the sub task will cover from this task
	 * @param taskName The name of the task. Or <code>null</code>
	 * @return A new sub task object
	 */
	ProgressMonitor newSubTask(int work, String taskName);
	
	/**
	 * The parent of this task (if it is not the root ProgressMonitor)
	 * @return the parent or <code>null</code> if this is the root {@link ProgressMonitor}
	 */
	ProgressMonitor getParent();
	/**
	 * Return a list of the hierarchical context (or path) of progress monitor.
	 * The first entry is the root {@link ProgressMonitor} and the last is this monitors {@link #getParent() parent}
	 */
	List<ProgressMonitor> getContext();
	/**
	 * Add a listener to be notified of future events
	 * @param listener The new listener
	 */
	void addUpdateListener(ProgressMonitorListener listener);
	/**
	 * Remove a listener to no longer be notified of future events
	 * @param listener The listener to remove
	 */
	void removeUpdateListener(ProgressMonitorListener listener);
}
