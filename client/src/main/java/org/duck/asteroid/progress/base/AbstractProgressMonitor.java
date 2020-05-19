package org.duck.asteroid.progress.base;

import org.duck.asteroid.progress.ProgressMonitor;
import org.duck.asteroid.progress.base.event.ProgressUpdateType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * An abstract base class for monitors. This handles work done, status and name.
 * It can also handle creating and maintaining sub tasks.
 */
public abstract class AbstractProgressMonitor implements ProgressMonitor {
	/** Default size of a progress monitor if {@link #setSize(long)} is not called */
	private static final long DEFAULT_SIZE = 1;
	/** The identity of this monitor (unique in the scope of it's parent) */
	protected final int id;
	/** the next ID for a sub task */
	protected AtomicInteger childId = new AtomicInteger(0);
	/** a collection of the active children of this monitor */
	protected CopyOnWriteArrayList<AbstractProgressMonitor> children = new CopyOnWriteArrayList<>();
	/** The current task name */
	protected final String taskName;
	/** The last set value of {@link #setStatus(String)} */
	protected String status = "";
	/** The amount of work currently done */
	protected AtomicLong workDone = new AtomicLong(0);
	/** The size of this monitor in work units, the total work */
	protected AtomicLong size = new AtomicLong(DEFAULT_SIZE);
	/** flag to indicate the work is done - a {@link #latchDone() latched} value used to ensure we only log our total work once to the parent */
	protected final AtomicBoolean done = new AtomicBoolean(false);

	public AbstractProgressMonitor(final int id, final String name) {
		this.id = id;
		this.taskName = name;
	}

	/**
	 * Gives an order to this and derived classes.
	 * Root monitor before all, siblings in ID order, everyone else greater
	 */
	@Override
	public int compareTo(ProgressMonitor o) {
     	// negative integer, zero, or a positive integer as this object is
		// less than, equal to, or greater than the specified object.
		// our parent is always less than us...
		if (getParent() == o) {
			return Integer.MIN_VALUE;
		}
		// siblings (same parent) are sorted by ID
		if (getParent() == o.getParent() && o instanceof AbstractProgressMonitor) {
			return this.id - ((AbstractProgressMonitor)o).id;
		}
		// otherwise always more than
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || ! (o instanceof AbstractProgressMonitor)) return false;
		AbstractProgressMonitor that = (AbstractProgressMonitor) o;
		return id == that.id &&
				Objects.equals(getParent(), that.getParent());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getParent(), id);
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
		// status only update
		if (!isDone()) {
			notifyListeners(this, ProgressUpdateType.STATUS);
		}
	}

	@Override
	public long getSize() {
		return size.get();
	}

	@Override
	public void setSize(long size) throws IllegalArgumentException {
		this.size.set(size);
		updateWork(workDone.get(), size, done.get());
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
	public long worked(long amount, String status) {
		amount = Math.max(0, amount); // min 0
		long size;
		boolean done;
		boolean set;
		long targetWorked; // calculate what we would like the work to be
		do {
			size = this.size.get();
			done = this.done.get();
			long worked = workDone.get();
			targetWorked = worked + amount;
			set = workDone.compareAndSet(worked, targetWorked);
			// if someone updated worked while we were computing the new value - it's not set - so try again...
		} while(!set);

		// do we need to post status
		if (status != null) {
			this.status = status;
		}

		updateWork(targetWorked, size, done);
		// return the newly updated amount of workdone (NOTE: it may have changed again by now)
		return targetWorked;
	}

	@Override
	public boolean isDone() {
		return workDone.get() >= size.get();
	}

	@Override
	public void done() {
		if (!isDone()) {
			long targetWorked;
			boolean set = false;
			boolean done;
			do {
				done = this.done.get();
				long worked = workDone.get();
				targetWorked = size.get();
				set = workDone.compareAndSet(worked, targetWorked);
				// if someone updated work while we were computing the new value & it was not set - try again...
			} while (!set);
			// log this update
			updateWork(targetWorked, targetWorked, done);
		}
	}

	/**
	 * Called whenever the work or size (or both) have changed -
	 * keeps done up-to-date
	 * handles notificiations (logUpdate)
	 * @param work the work to log
	 * @param size the size to log
	 * @param done the "done" state when this update was made
	 */
	protected void updateWork(long work, long size, boolean done) {
		if(done) {
			latchDone();
		}
		else if (work > 0) {
			notifyListeners(this, ProgressUpdateType.WORK);
			if (isDone()) {
				latchDone();
			}
		}
	}

	/**
	 * Called when a method percieves the monitor is now done
	 * used to latch the done state and fire one DONE event, and give subclasses a chance to "tidy up"
	 */
	private void latchDone() {
		boolean updated = this.done.compareAndSet(false, true);
		// we would only fail to update - if someone got here first
		if (updated) {
			notifyListeners(this, ProgressUpdateType.DONE);
			onDone();
		}
	}


	/**
	 * Notify any listeners of an update
	 * @param source the source of the update
	 * @param updateType the type of update being made
	 */
	public abstract void notifyListeners(ProgressMonitor source, ProgressUpdateType updateType);

	/**
	 * Called exactly once when this transitions to the done state (used to do update in subtasks!!)
	 */
	protected abstract void onDone();

	@Override
	public double getFractionDone() {
		return (double)workDone.get() / (double)size.get();
	}

	@Override
	public ProgressMonitor newSubTask(String name) {
		return newSubTask(name, DEFAULT_SIZE);
	}

	@Override
	public ProgressMonitor newSubTask(String name, long work) {
		SubTaskProgressMonitor subTask = new SubTaskProgressMonitor(this, this.childId.getAndIncrement(), work, name);
		children.add(subTask);
		return subTask;
	}

	protected void removeSubTask(SubTaskProgressMonitor subTask) {
		children.remove(subTask);
	}

	/**
	 * Recursive implementation behind {@link BaseProgressMonitor#getAllActive()}
	 * @param active the list of active monitors to append to
	 */
	protected void appendActive(List<ProgressMonitor> active) {
		if (!isDone()) {
			active.add(this);
			for (AbstractProgressMonitor child : children) {
				child.appendActive(active);
			}
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " {" +
				"taskName='" + taskName + '\'' +
				", status='" + status + '\'' +
				", workDone=" + workDone +
				", size=" + size +
				'}';
	}


}