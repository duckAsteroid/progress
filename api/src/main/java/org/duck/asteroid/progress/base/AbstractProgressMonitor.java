package org.duck.asteroid.progress.base;

import java.util.ArrayList;

import org.duck.asteroid.progress.ProgressMonitor;
import org.duck.asteroid.progress.ProgressMonitorListener;
import org.duck.asteroid.progress.ProgressMonitorListener.EventType;

/**
 * An abstract base class for all monitors.
 */
public abstract class AbstractProgressMonitor implements ProgressMonitor {

	/** The total work being tracked */
	protected int totalWork = 100;
	/** The work done (so far) */
	protected int workDone = 0;
	/** The current task name */
	protected String taskName = "";
	/** The last set value of {@link #notify(String)} */
	protected String status = "";
	/** A list of current listeners */
	protected ArrayList<ProgressMonitorListener> listeners = new ArrayList<ProgressMonitorListener>(1);

	public AbstractProgressMonitor() {
		super();
	}

	public void begin(int total) {
		this.totalWork = total;
		this.workDone = 0;
		notify(EventType.BEGIN);
		logUpdate(this);
	}

	public int getTotalWork() {
		return totalWork;
	}

	public int getWorkDone() {
		return workDone;
	}

	public void setWorkDone(int done) {
		int worked = done - workDone;
		if (done > 0) {
			int work = Math.min(worked, getWorkRemaining());
			worked(work, null);
		}
	}

	public int getWorkRemaining() {
		return totalWork - workDone;
	}

	public void setWorkRemaining(int remaining) {
		if (remaining >= 0 && remaining <= getTotalWork()) {
			setWorkDone(totalWork - remaining);
		}
	}

	public void done() {
		worked(getWorkRemaining(), null);
	}

	/**
	 * This is the main work update method.
	 * Both {@link #setWorkDone(int)} and {@link #setWorkRemaining(int)} call this
	 */
	public synchronized void worked(int work, String status) {
		if (work > 0) {
			workDone += work;
			if (status != null) {
				this.status = status;
			}
			notify(EventType.WORK_DONE);
			logUpdate(this);
			checkCompleted();
		}
	}

	private void checkCompleted() {
		if (isWorkComplete()) {
			notify(EventType.COMPLETED);
		}
	}

	public boolean isWorkComplete() {
		return workDone == totalWork; 
	}

	public double getFractionDone() {
		return (double)workDone / (double)totalWork;
	}

	public String getTaskName() {
		return taskName;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void notify(String status) {
		this.status = status;
		notify(EventType.NOTIFICATION);
		logUpdate(this);
	}
	
	public ProgressMonitor newSubTask(int work, String taskName) {
		return new SubTaskProgressMonitor(this, work, taskName);
	}
	
	public synchronized void addUpdateListener(ProgressMonitorListener listener) {
		listeners.add(listener);
	}
	
	public synchronized void removeUpdateListener(ProgressMonitorListener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * Notify current listeners of an event in this monitor
	 * @param type The type of event
	 */
	protected synchronized void notify(ProgressMonitorListener.EventType type) {
		ArrayList<ProgressMonitorListener> tmp = new ArrayList<ProgressMonitorListener>(listeners);
		for(ProgressMonitorListener listener : tmp) {
			listener.progressUpdated(type, this);
		}
	}
	/**
	 * called whenever some udpate needs to be logged to UI in the chain of monitors
	 * @param child The child that raised the log
	 */
	protected abstract void logUpdate(AbstractProgressMonitor child);

	@Override
	public String toString() {
		return ProgressFormat.DEFAULT.format(this);
	}

}