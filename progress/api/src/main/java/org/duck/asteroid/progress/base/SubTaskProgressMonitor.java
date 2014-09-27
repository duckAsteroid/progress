package org.duck.asteroid.progress.base;

import org.duck.asteroid.progress.ProgressMonitor;
/**
 * A sub task progress monitor - that delegates to parent 
 */
public class SubTaskProgressMonitor extends BaseProgressMonitor {
	
	protected BaseProgressMonitor parent;
	
	protected int totalParentWork;
	
	public SubTaskProgressMonitor(BaseProgressMonitor parent, int totalParentWork) {
		this.parent = parent;
		this.totalParentWork = totalParentWork;
	}

	public ProgressMonitor getParent() {
		return parent;
	}
	
	public void notify(String status) {
		parent.notify();
	}

	public boolean isCancelled() {
		return parent.isCancelled();
	}

	public void setCancelled(boolean cancelled) {
		parent.setCancelled(cancelled);
	}

	public ProgressMonitor newSubTask(int work, String taskName) {
		return new SubTaskProgressMonitor(this, work);
	}
	
	@Override
	public void worked(int work) {
		super.worked(work);
		int parentWork = (int)(((double) work / (double)totalWork) * totalParentWork);
		parent.worked(parentWork);
	}

	@Override
	public void updated() {
		parent.updated();
	}
	
	
	
}
