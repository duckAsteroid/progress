package org.duck.asteroid.progress;

/**
 * The interface to an object an application can use to report work on a task.
 */
public interface ProgressMonitor {

	/**
	 * The parent of this task (if it is not the root ProgressMonitor)
	 * @return the parent or <code>null</code> if this is the root {@link ProgressMonitor}
	 */
	public ProgressMonitor getParent();
	
	/**
	 * Begin this task and set the {{@link #getTotalWork() total amount work} that this task entails.
	 * Note: the default total work is 100.
	 * Resets the {@link #getWorkDone()} to 0.
	 * @param total the total work in this task 
	 */
	public void begin(int total);
	
	/**
	 * The total amount work that this task entails.
	 * The default (if not provided in {@link #begin(int, String)} is 100)
	 * @return The total work. A positive integer
	 */
	public int getTotalWork();
	
	/**
	 * The work done so far (always less than or equal to {@link #getTotalWork()})
	 * @return The work done so far. A positive integer.
	 */
	public int getWorkDone();
	
	/**
	 * Set the absolute work done so far. This can be less than the current {@link #getWorkDone()}.
	 * Attempting to set this value larger than {@link #getTotalWork()} will result 
	 * in {@link #getWorkDone()} == {@link #getTotalWork()}.
	 * Attempting to set this value to less than zero will result in {@link #getWorkDone()} == 0. 
	 * @param done The work done so far.
	 */
	public void setWorkDone(int done);
	
	/**
	 * The remaining work to be done. Always less than {@link #getTotalWork()}.
	 * @return The work remaining
	 */
	public int getWorkRemaining();
	
	/**
	 * Set the absolute work remaining. This can be less than the current {@link #getWorkRemaining()}.
	 * Attempting to set this value larger than {@link #getTotalWork()} will result 
	 * in {@link #getWorkRemaining()} == {@link #getTotalWork()}.
	 * Attempting to set this value to less than zero will result in {@link #getWorkRemaining()} == 0.  
	 * @param remaining The new amount of work remaining.
	 */
	public void setWorkRemaining(int remaining);
	
	/**
	 * Log an amount of work being done. 
	 * @param work The work that has been done.
	 */
	public void worked(int work);
	
	/**
	 * Clients should call this to indicate all work on this task is complete
	 */
	public void done();
	
	/**
	 * Is the work reported complete (e.g. does {@link #getWorkDone()} == {@link #getTotalWork()})
	 * @return true if the work is complete
	 */
	public boolean isWorkComplete();
	
	/**
	 * Returns the value of {@link #getWorkDone()}/{@link #getTotalWork()} as a double fraction.
	 * @return The fraction of work done
	 */
	public double getFractionDone();
	
	/**
	 * Notify users of the status of this task. There are no guarantees this message will ever
	 * be displayed!
	 * @param status The task status notification message
	 */
	public void notify(String status);
	
	/**
	 * Has the task being reported been cancelled.
	 * @return true if cancelled
	 */
	public boolean isCancelled();
	/**
	 * Change the cancelled state of the task being monitored
	 * @param cancelled the new cancelled state
	 */
	public void setCancelled(boolean cancelled);

	/**
	 * Add a sub task to this task. When completed this sub task will contribute the given amount of
	 * work to this task.
	 * @param work The amount of work the sub task will cover from this task
	 * @param taskName The name of the task
	 * @return A new sub task object
	 */
	public ProgressMonitor newSubTask(int work, String taskName);
}
