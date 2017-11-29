package org.duck.asteroid.progress;

import java.util.List;

/**
 * The interface to an object an application can use to report work on a task.
 * A task only has a fractional completion state (0 - 1) represented as a double.
 */
public interface ProgressMonitor {
	/**
	 * The parent monitor of this monitor (if any)
	 * @return the parent monitor or <code>null</code> if this is the root
	 */
	ProgressMonitor getParent();

	/**
	 * The path from this monitor to the root through all parent contexts.
	 * @return a list of parent contexts (all the way to the root); an empty list if this is the root.
	 */
	List<ProgressMonitor> getContext();
	/**
	 * The name of this task set by the {@link #getParent() parent} (if any)
	 * @return the task name (or empty String)
	 */
	String getTaskName();
	/**
	 * The last {@link #setStatus(String) notified} status
	 * @return the task status (or empty String)
	 */
	String getStatus();
	/**
	 * Notify users (if possible) of the status of this task (without changing the fraction done).
	 * @param status The task status notification message
	 */
	void setStatus(String status);

	/**
	 * The "size" of this task - by default this is <code>1</code>
	 * @return the "relative" size of this task
	 */
	long getSize();

	/**
	 * Modify the overall size of this task. If the current {@link #getWorkDone()} is already more than
	 * this then the monitor will be {@link #isDone() done}.
	 * The size cannot be modified to less than 1.
	 * Note: modifying the size of a subtask - does not influence the work it contributes to the parent task
	 * @param size the new size of this task
	 * @throws IllegalArgumentException if the size is less than 1
	 */
	void setSize(long size) throws IllegalArgumentException;

	/**
	 * The current amount of work done. This is always a positive number or zero.
	 * It may be more than the size of this task (which simply means the task is done)
	 * @return the current amount of work done
	 */
	long getWorkDone();

	/**
	 * Log an amount of work and (optionally) update the status in a single operation
	 * @param amount the amount of work done
	 * @param status a status message to set (if not <code>null</code>)
	 * @returns the value of {@link #getWorkDone()} as a result of this new work
	 * @throws IllegalArgumentException if the amount of work is less than 0
	 */
	long worked(long amount, String status);
	/**
	 * Log an amount of work
	 * @param amount the amount of work done
	 * @returns the value of {@link #getWorkDone()} as a result of this new work
	 * @throws IllegalArgumentException if the amount of work is less than 0
	 */
	long worked(long amount);

	/**
	 * Called to set the work done to the size of the task (at this point in time)
	 * Subsequent modifications to {@link #getSize()} may alter this.
	 */
	void done();

	/**
	 * Is the work reported complete (i.e. is {@link #getWorkDone()} &gt;= {@link #getSize()}} )
	 * @return true if the work is complete/done
	 */
	boolean isDone();
	
	/**
	 * Has the task being reported been cancelled.
	 * @return true if cancelled
	 */
	boolean isCancelled();
	/**
	 * Change the cancelled state of the task being monitored (and that of all sub-tasks)
	 * @param cancelled the new cancelled state
	 */
	void setCancelled(boolean cancelled);

	/**
	 * Create a sub task of this - which when {@link #isDone()} equates to the given amount of work in
	 * this task.
	 * @param name the name of the sub task
	 * @param size the relative size of this new task
	 * @return a new progress monitor for the sub task
	 * @throws IllegalStateException If this task is already {@link #isDone() done}
	 */
	ProgressMonitor newSubTask(String name, long size);

	/**
	 * Create a sub task of this - which when {@link #isDone()} equates to a single (1) unit of work in
	 * this task.
	 * Equivalent to calling {@link #newSubTask(String, long)} with the given <code>name</code> and a
	 * <code>size = 1</code>
	 * @param name the name of the sub task
	 * @return a new progress monitor for the sub task
	 * @throws IllegalStateException If this task is already {@link #isDone() done}
	 */
	ProgressMonitor newSubTask(String name);

	/**
	 * The {@link #getWorkDone()} as a fraction of the {@link #getSize()} for this task.
	 * NOTE: this is intended to be in the range 0 - 1; however if the work done exceeds
	 * the size of this task - the fraction will be &gt; 1
	 * @return the fraction done
	 */
	double getFractionDone();


}
