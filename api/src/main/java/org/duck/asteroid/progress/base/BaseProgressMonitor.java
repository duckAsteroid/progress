package org.duck.asteroid.progress.base;

import org.duck.asteroid.progress.ProgressMonitor;
import org.duck.asteroid.progress.base.frac.AbstractFractionalProgress;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A basic implementation of the progress monitor interface.
 * The major drawback of this implementation is that it tracks but does not report
 * progress to the user. It may be used in other classes for the {@link #toString()}
 * form?
 */
public class BaseProgressMonitor extends AbstractProgressMonitor implements ProgressMonitor {
	public static final double NOT_STARTED = 0.0d;
	public static final double DONE = 1.0d;

	/** Has cancellation been requested */
	protected AtomicBoolean cancelled = new AtomicBoolean(false);

	/** The fractional work done */
	protected AtomicLong workDoneFraction = new AtomicLong(Double.doubleToLongBits(NOT_STARTED));

	public BaseProgressMonitor() {
		super();
	}

	public BaseProgressMonitor(final String name) {
		super(name);
	}

	@Override
	public double getFractionDone() {
		return Double.longBitsToDouble(workDoneFraction.get());
	}

	@Override
	public void setFractionDone(final double fractionDone) {
		setFractionDoneInternal(fractionDone, null);
	}

	@Override
	public void setFractionDoneInternal(final double fractionDone, final AbstractProgressMonitor ignored) {
		workDoneFraction.set(Double.doubleToLongBits(fractionDone));
		for (AbstractFractionalProgress<?> projection : projections) {
			if (ignored != projection) {
				projection.setFractionDoneInternal(fractionDone, ignored);
			}
		}
		logUpdate(this);
	}

	@Override
	public void logUpdate(ProgressMonitor source) {
		// NO-OP - subclasses may hook in here
	}

	@Override
	public boolean isWorkComplete() {
		return Double.longBitsToDouble(workDoneFraction.get()) >= DONE;
	}

	@Override
	public void done() {
		setFractionDone(DONE);
	}

	/**
	 * Always returns <code>null</code>
	 */
	public ProgressMonitor getParent() {
		return null;
	}
	/**
	 * Always returns an empty list.
	 */
	public List<ProgressMonitor> getContext() {
		return Collections.emptyList();
	}

	public boolean isCancelled() {
		return cancelled.get();
	}

	public synchronized void setCancelled(boolean cancelled) {
		this.cancelled.set(cancelled);
	}



}
