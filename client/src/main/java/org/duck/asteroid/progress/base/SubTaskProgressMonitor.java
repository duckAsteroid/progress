package org.duck.asteroid.progress.base;

import org.duck.asteroid.progress.ProgressMonitor;
import org.duck.asteroid.progress.base.event.ProgressUpdateType;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A sub task progress monitor. Tracks progress and updates the parent when work is done
 * A sub task does not update the parent until the work is done; and will only update once,
 * as soon as the work is done. Modifying the size after this event will have no effect and
 * no further updates will be passed to the parent.
 */
public class SubTaskProgressMonitor extends AbstractProgressMonitor {
	/** The context of this monitor */
	protected final LinkedList<ProgressMonitor> context;
	/** parent (supplied in constructor) */
	protected final AbstractProgressMonitor parent;
	/** The amount of work that this monitor contributes to it's parent when done */
	protected final long totalParentWork;

	/**
	 * Create a sub task for a given parent monitor
	 * @param parent The parent to report on
	 * @param totalParentWork The total work in the parent that this complete task represents
	 * @param name The name of this task
	 */
	public SubTaskProgressMonitor(AbstractProgressMonitor parent, int id, final long totalParentWork, final String name) {
		super(id, name);
		if (parent == null) { 
			throw new IllegalArgumentException("Parent cannot be null");
		}
		this.parent = parent;
		if (totalParentWork < 0) {
			throw new IllegalArgumentException("Parent work must be zero or positive");
		}
		this.totalParentWork = totalParentWork;
		
		// calculate this monitors context
		this.context = new LinkedList<>(parent.getContext());
		this.context.add(parent);
	}

	@Override
	protected void onDone() {
		// we are done - log the total work in the parent
		parent.worked(this.totalParentWork, taskName);
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
	public void notifyListeners(ProgressMonitor source, ProgressUpdateType updateType) {
		parent.notifyListeners(source, updateType);
	}
}
