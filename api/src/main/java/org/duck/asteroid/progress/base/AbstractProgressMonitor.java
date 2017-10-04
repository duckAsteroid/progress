package org.duck.asteroid.progress.base;

import org.duck.asteroid.progress.FractionalProgress;
import org.duck.asteroid.progress.ProgressMonitor;
import org.duck.asteroid.progress.base.format.ProgressFormat;
import org.duck.asteroid.progress.base.frac.AbstractFractionalProgress;
import org.duck.asteroid.progress.base.frac.FractionalProgressInteger;
import org.duck.asteroid.progress.base.frac.FractionalProgressLong;

import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * An abstract base class for all monitors. This handles listeners status and name.
 */
public abstract class AbstractProgressMonitor implements ProgressMonitor {

	/** The current task name */
	protected final String taskName;
	/** The last set value of {@link #notify(String)} */
	protected String status = "";
	/** A list of the fractional projections of this progress */
	protected ConcurrentLinkedDeque<AbstractFractionalProgress<?>> projections = new ConcurrentLinkedDeque<>();

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
	public void notify(String status) {
		this.status = status;
		logUpdate(this);
	}

	@Override
	public ProgressMonitor split(double work, String taskName) {
		SubTaskProgressMonitor subTask = new SubTaskProgressMonitor(this, work, taskName);
		return subTask;
	}

	@Override
	public FractionalProgress<Integer> asInteger(int total) {
		FractionalProgressInteger progressInteger = new FractionalProgressInteger(this, total);
		projections.add(progressInteger);
		return progressInteger;
	}

	@Override
	public FractionalProgress<Long> asLong(long total) {
		FractionalProgressLong progressLong = new FractionalProgressLong(this, total);
		projections.add(progressLong);
		return progressLong;
	}

	/**
	 * A mechanism for projections and core progress to communicate updates without
	 * circularity
	 * @param fractionDone the fraction done
	 * @param ignored the source of the internal update (if any) - will not be notified
	 */
	public abstract void setFractionDoneInternal(final double fractionDone, AbstractProgressMonitor ignored);

	/**
	 * A method for the root monitor to hook into updates in the tree
	 * @param source the source of the update
	 */
	public abstract void logUpdate(ProgressMonitor source);

	@Override
	public String toString() {
		return ProgressFormat.DEFAULT.format(this);
	}

}