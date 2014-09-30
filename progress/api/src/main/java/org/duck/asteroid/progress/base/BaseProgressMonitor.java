package org.duck.asteroid.progress.base;

import java.util.Collections;
import java.util.List;

import org.duck.asteroid.progress.ProgressMonitor;
import org.duck.asteroid.progress.ProgressMonitorListener.EventType;
/**
 * A basic implementation of the progress monitor interface.
 * The major drawback of this implementation is that it tracks but does not report
 * progress to the user. It may be used in other classes for the {@link #toString()}
 * form?
 */
public class BaseProgressMonitor extends AbstractProgressMonitor implements ProgressMonitor {
	
	/** Has cancellation been requested */
	protected boolean cancelled = false;
	
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
		return cancelled;
	}

	public synchronized void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
		notify(EventType.CANCELLED);
	}
	/**
	 * This implementation quietly does nothing!
	 */
	@Override
	protected void logUpdate(AbstractProgressMonitor child) {
		// no-op
	}
}
