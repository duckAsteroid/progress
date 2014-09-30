package org.duck.asteroid.progress.base;

import java.text.NumberFormat;

import org.duck.asteroid.progress.ProgressMonitor;

public class ProgressFormat {
	/** A default (everything on) format for monitors */
	public static final ProgressFormat DEFAULT = new ProgressFormat(true, true, true, true, true);
	
	private boolean showTask;
	private boolean showWork; 
	private boolean showPercent;
	private boolean showStatus;
	private boolean showParents;
	
	public ProgressFormat(boolean showParents, boolean showTask, boolean showWork,
			boolean showPercent, boolean showStatus) {
		super();
		this.showParents = showParents;
		this.showTask = showTask;
		this.showWork = showWork;
		this.showPercent = showPercent;
		this.showStatus = showStatus;
	}

	// FIXME - use message format to control output in detail
	
	/** A number format for percentages */
	protected static final NumberFormat PERCENT_FMT = NumberFormat.getPercentInstance();
	
	public String format(ProgressMonitor monitor) {
		final StringBuilder sb = new StringBuilder();
	
		if (showParents && monitor.getParent() != null) {
			sb.append(format(monitor.getParent())).append("> ");
		}
		
		if(showTask) {
			String taskName = monitor.getTaskName();
			if ( taskName != null && taskName.length() > 0) {
				sb.append(taskName).append(" ");
			}
		}
		
		if (showWork) {
			sb.append("[").append(monitor.getWorkDone()).append("/").append(monitor.getTotalWork()).append("] ");
		}
		
		if (showPercent) {
			sb.append("(").append(PERCENT_FMT.format(monitor.getFractionDone())).append(") ");
		}
		
		if (showStatus) {
			String status = monitor.getStatus();
			if (status != null && status.length() > 0) {	
				sb.append("- ").append(status);
			}
		}
	
		return sb.toString();
		}
}
