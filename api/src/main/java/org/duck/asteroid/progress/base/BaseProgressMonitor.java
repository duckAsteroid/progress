package org.duck.asteroid.progress.base;

import org.duck.asteroid.progress.ProgressMonitor;

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

	/** Has cancellation been requested */
	protected AtomicBoolean cancelled = new AtomicBoolean(false);

	public BaseProgressMonitor() {
		super();
	}

	public BaseProgressMonitor(final String name) {
		super(name);
	}

	public BaseProgressMonitor(final String name, final long size) {
		super(name);
		this.setSize(size);
	}

	public BaseProgressMonitor(final long size) {
		this.setSize(size);
	}

	@Override
	public void logUpdate(ProgressMonitor source) {
		// NO-OP - subclasses may hook in here
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
