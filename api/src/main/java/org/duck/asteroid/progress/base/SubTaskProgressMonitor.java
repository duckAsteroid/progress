package org.duck.asteroid.progress.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.duck.asteroid.progress.ProgressMonitor;

/**
 * A sub task progress monitor. Tracks progress and updates the parent when work is done
 * A sub task does not update the parent until the work is done; and will only update only once,
 * as soon as the work is done. Modifying the size after this event will have no effect and
 * no further updates will be passed to the parent.
 */
public class SubTaskProgressMonitor extends AbstractProgressMonitor {
	/** The context of this monitor */
	protected final ArrayList<ProgressMonitor> context;
	/** parent (supplied in constructor) */
	protected final AbstractProgressMonitor parent;
	/** The total (fractional) work that this monitor contributes to it's parent when completed */
	protected final long totalParentWork;
	/** Is the work done - used to ensure we only log our total work once to the parent */
	protected final AtomicBoolean done = new AtomicBoolean(false);

	/**
	 * Create a sub task for a given parent monitor
	 * @param parent The parent to report on
	 * @param totalParentWork The total work in the parent that this complete task represents
	 * @param name The name of this task
	 */
	public SubTaskProgressMonitor(AbstractProgressMonitor parent, final long totalParentWork, final String name) {
		super(name);
		if (parent == null) { 
			throw new IllegalArgumentException("Parent cannot be null");
		}
		this.parent = parent;
		if (totalParentWork < 0) {
			throw new IllegalArgumentException("Parent work must be zero or positive");
		}
		this.totalParentWork = totalParentWork;
		
		// calculate this monitors context
		this.context = new ArrayList<ProgressMonitor>(parent.getContext());
		this.context.add(parent);
	}

	@Override
	public long worked(long amount, String status) {
		final long workDone = super.worked(amount, status);
		// does this new value for our local work done mean this task is done?
		if (!done.get() && workDone >= getSize()) {
			boolean updated = done.compareAndSet(false, true);
			// we would only fail to update - if someone got here first
			if (updated) {
				// we are done - log the total work in the parent
				// the above code ensures this can happen only once!
				parent.worked(this.totalParentWork, taskName);
			}
		}
		return workDone;
	}

	@Override
	public void done() {
		super.done();
		if (!done.get()) {
			boolean updated = done.compareAndSet(false, true);
			// we would only fail to update - if someone got here first
			if (updated) {
				// we are done - log the total work in the parent
				// the above code ensures this can happen only once!
				parent.worked(this.totalParentWork, taskName);
			}
		}
	}

	@Override
	public ProgressMonitor getParent() {
		return parent;
	}

	@Override
	public List<ProgressMonitor> getContext() {
		return Collections.unmodifiableList(context);
	}

	@Override
	public boolean isCancelled() {
		return parent.isCancelled();
	}

	@Override
	public void setCancelled(boolean cancelled) {
		parent.setCancelled(cancelled);
	}

	@Override
	public void logUpdate(ProgressMonitor source) {
		parent.logUpdate(this);
	}
}
