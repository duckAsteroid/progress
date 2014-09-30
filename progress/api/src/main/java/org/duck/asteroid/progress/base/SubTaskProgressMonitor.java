package org.duck.asteroid.progress.base;

import java.util.ArrayList;
import java.util.List;

import org.duck.asteroid.progress.ProgressMonitor;

/**
 * A sub task progress monitor. Tracks progress and updates the parent of work done
 */
public class SubTaskProgressMonitor extends AbstractProgressMonitor {
	/** The context of this monitor */
	protected ArrayList<ProgressMonitor> context;
	/** parent (supplied in constructor) */
	protected AbstractProgressMonitor parent;
	/** The total work that this monitor contributes to it's parent when completed */
	protected int totalParentWork;
	
	/**
	 * Create a sub task for a given parent monitor
	 * @param parent The parent to report on
	 * @param totalParentWork The total work in the parent that this complete task represents
	 * @param name The name of this task
	 */
	public SubTaskProgressMonitor(AbstractProgressMonitor parent, int totalParentWork, String name) {
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
		
		this.taskName = name;
	}

	public ProgressMonitor getParent() {
		return parent;
	}
	
	public List<ProgressMonitor> getContext() {
		return context;
	}

	public boolean isCancelled() {
		return parent.isCancelled();
	}

	public void setCancelled(boolean cancelled) {
		parent.setCancelled(cancelled);
	}
	
	@Override
	public void worked(int work, String status) {
		// track local work
		super.worked(work, status);
		// calculate equivalent parent work
		int parentWork = (int)(((double) work / (double)totalWork) * totalParentWork);
		// update parent work
		parent.worked(parentWork, null);
	}

	@Override
	protected void logUpdate(AbstractProgressMonitor child) {
		parent.logUpdate(child);
	}
}
