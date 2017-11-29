package org.duck.asteroid.progress.base;

import org.duck.asteroid.progress.ProgressMonitor;
import org.duck.asteroid.progress.base.format.ProgressFormat;

import java.util.concurrent.atomic.AtomicLong;

/**
 * An abstract base class for all monitors. This handles listeners status and name.
 */
public abstract class AbstractProgressMonitor implements ProgressMonitor {
	public static long DEFAULT_SIZE = 1;
	/** The current task name */
	protected final String taskName;
	/** The last set value of {@link #setStatus(String)} */
	protected String status = "";
	/** The work done */
	protected AtomicLong workDone = new AtomicLong(0);
	/** The size */
	protected AtomicLong size = new AtomicLong(DEFAULT_SIZE);

	public AbstractProgressMonitor() {
		this.taskName = "";
	}

	public AbstractProgressMonitor(final String name) {
		this.taskName = name;
	}

	@Override
	public String getTaskName() {
		return taskName;
	}

	@Override
	public String getStatus() {
		return status;
	}

	@Override
	public void setStatus(String status) {
		this.status = status;
		logUpdate(this);
	}
	@Override
	public boolean isDone() {
		return workDone.get() >= size.get();
	}

	@Override
	public long getSize() {
		return size.get();
	}

	@Override
	public void setSize(long size) throws IllegalArgumentException {
		this.size.set(size);
	}

	@Override
	public long getWorkDone() {
		return workDone.get();
	}

	@Override
	public long worked(long amount) {
		return worked(amount, null);
	}

	@Override
	public void done() {
		boolean updated = false;
		do {
			long worked = workDone.get();
			long targetWorked = size.get();
			// if someone updated work while we were computing the new value - try again...
			updated = workDone.compareAndSet(worked, targetWorked);
		} while(!updated);
		// log this update
		logUpdate(this);
	}

	@Override
	public long worked(long amount, String status) {
		if (amount < 0) {
			throw new IllegalArgumentException("Amount of work cannot be less than zero");
		}
		boolean updated;
		long targetWorked; // calculate what we would like the work to be
		do {
			long worked = workDone.get();
			targetWorked = worked + amount;
			// if someone updated work while we were computing the new value - try again...
			updated = workDone.compareAndSet(worked, targetWorked);
		} while(!updated);
		// do we need to post status
		if (status != null) {
			this.status = status;
		}
		// log this update
		logUpdate(this);
		// return the newly updated amount of workdone (NOTE: it may have changed again by now)
		return targetWorked;
	}

	@Override
	public double getFractionDone() {
		return (double)workDone.get() / (double)size.get();
	}

	@Override
	public ProgressMonitor newSubTask(String name, long size) {
		return new SubTaskProgressMonitor(this, size, name);
	}

	@Override
	public ProgressMonitor newSubTask(String name) {
		return newSubTask(name, DEFAULT_SIZE);
	}

	/**
	 * A method for the root monitor to be notified of updates in the tree of monitors
	 * @param source the source of the update
	 */
	public abstract void logUpdate(ProgressMonitor source);

	@Override
	public String toString() {
		return ProgressFormat.DEFAULT.format(this);
	}


}