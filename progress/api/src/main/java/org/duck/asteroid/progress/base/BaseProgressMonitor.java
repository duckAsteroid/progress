package org.duck.asteroid.progress.base;

import org.duck.asteroid.progress.ProgressMonitor;

public class BaseProgressMonitor implements ProgressMonitor {
	
	protected int totalWork = 100;
	protected int workDone = 0;
	protected boolean cancelled = false;
	
	public ProgressMonitor getParent() {
		return null;
	}

	public void begin(int total) {
		this.totalWork = total;
		this.workDone = 0;
		updated();
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
			worked(work);
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
		worked(getWorkRemaining());
	}
	/**
	 * This is the main work update method.
	 * Both {@link #setWorkDone(int)} and {@link #setWorkRemaining(int)} call this
	 */
	public synchronized void worked(int work) {
		if (work > 0) {
			workDone += work;
		}
		updated();
	}
	
	public boolean isWorkComplete() {
		return workDone == totalWork; 
	}

	public double getFractionDone() {
		return (double)workDone / (double)totalWork;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public ProgressMonitor newSubTask(int work, String taskName) {
		return new SubTaskProgressMonitor(this, work);
	}
	
	public void notify(String status) {
		
	}

	public void updated() {
		
	}
	
	// TODO implement toString, equals hashCode etc.

}
