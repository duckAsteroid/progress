package org.duck.asteroid.progress;

import java.util.List;

/**
 * The interface to an object an application can use to report work on a task.
 * A task only has a fractional completion state (0 - 1) represented as a double.
 */
public interface ProgressMonitor {
	/**
	 * The fraction of work done nominally in the range zero (not started) to one (done).
	 * NOTE: the fraction done may be more than one, it is best to use {@link #isWorkComplete}
	 * @return The fraction of work done as a double
	 */
	double getFractionDone();

	/**
	 * Modify the fraction of work done. Typically in the bounds zero (not started) to one (done).
	 * This will generate a
	 * If this modification causes {@link #isWorkComplete()} to be true a
	 * @param fractionDone the new fraction
	 */
	void setFractionDone(double fractionDone);

	/**
	 * The name of this task set by the {@link #getParent() parent} (if any)
	 * @return the task name
	 */
	String getTaskName();
	/**
	 * The last {@link #notify(String) notified} status
	 * @return the task status
	 */
	String getStatus();
	/**
	 * Notify users (if possible) of the status of this task (without changing the fraction done).
	 * This will generate a
	 * @param status The task status notification message
	 */
	void notify(String status);
	
	/**
	 * Is the work reported complete (e.g. is {@link #getFractionDone()} more than or equal to one)
	 * @return true if the work is complete
	 */
	boolean isWorkComplete();
	/**
	 * Clients may call this to indicate all work on this task is complete.
	 * Equivalent to calling {@link #setFractionDone(double)} with a value more than or equal to one.
	 */
	void done();
	
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
	 * Create an fractional integer based projection of progress on this monitor - tracking progress up to the given
	 * total (denominator).
	 * The returned delegate simply calls {@link #setFractionDone(double)} on this instance.
	 * It is not recommended to create more than one {@link FractionalProgress} for any given {@link ProgressMonitor}.
	 * Over reporting of progress will result
	 * @param total the total work in this task
	 */
	FractionalProgress<Integer> asInteger(int total);
	/**
	 * Create an fractional {@link Long} based projection of progress on this monitor - tracking progress up to the given
	 * total (denominator).
	 * The returned delegate simply calls {@link #setFractionDone(double)} on this instance.
	 * It is not recommended to create more than one {@link FractionalProgress} for any given {@link ProgressMonitor}.
	 * Over reporting of progress will result
	 * @param total the total work in this task
	 */
	FractionalProgress<Long> asLong(long total);

	/**
	 * Create a monitor that represents some subset of this monitor. In other words when the newly created monitor is
	 * 100% complete (1.0) - it will contribute the given amount of work to this progress.
	 *
	 * Work on the newly created subtask is propagated instantly to this monitor (the parent); however
	 * changes on this progress directly are not propagated to the subtask. For example if the split sub task represents
	 * 0.5
	 * @param work The amount of work the sub task will contribute to this monitor
	 * @param taskName The name of the task. Or <code>null</code>
	 * @return A new sub task object
	 */
	ProgressMonitor split(double work, String taskName);
	
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
     * Add a fractional amount of work to the overall progress.
     * @param amount the fractional amount of work done (this is added to the current work)
     */
    void fractionWorked(double amount);
}
