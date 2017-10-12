package org.duck.asteroid.progress.base;

import java.util.ArrayList;
import java.util.List;

import org.duck.asteroid.progress.ProgressMonitor;

/**
 * A sub task progress monitor. Tracks progress and updates the parent of work done
 */
public class SubTaskProgressMonitor extends BaseProgressMonitor {
	/** The context of this monitor */
	protected final ArrayList<ProgressMonitor> context;
	/** parent (supplied in constructor) */
	protected final AbstractProgressMonitor parent;
	/** The total (fractional) work that this monitor contributes to it's parent when completed */
	protected final double totalParentWork;
	
	/**
	 * Create a sub task for a given parent monitor
	 * @param parent The parent to report on
	 * @param totalParentWork The total work in the parent that this complete task represents
	 * @param name The name of this task
	 */
	public SubTaskProgressMonitor(AbstractProgressMonitor parent, final double totalParentWork, final String name) {
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
	public void setFractionDone(double fractionDone) {
		// update our own internal work fraction 0 - 1
		super.setFractionDone(fractionDone);
		// and update the parent
		parent.fractionWorked(totalParentWork * fractionDone);
	}

	@Override
	public void fractionWorked(double amount) {
		super.fractionWorked(amount); // update our internal work done...
		parent.fractionWorked(totalParentWork * amount);
	}

	@Override
	public void done() {
		double remainder = super.internalDone(); // update our internal state
		fractionWorked(remainder);
	}

	@Override
	public ProgressMonitor split(double work, String taskName) {
		SubTaskProgressMonitor subTask = new SubTaskProgressMonitor(parent, work, taskName);
		return subTask;
	}

	@Override
	public ProgressMonitor getParent() {
		return parent;
	}

	@Override
	public List<ProgressMonitor> getContext() {
		return context;
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
