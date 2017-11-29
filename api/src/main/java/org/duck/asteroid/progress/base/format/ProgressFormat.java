package org.duck.asteroid.progress.base.format;

import org.duck.asteroid.progress.ProgressMonitor;

import java.text.NumberFormat;

/**
 * A class that turns a {@link ProgressMonitor} into a {@link String} form for human consumption
 */
public class ProgressFormat {
	/** A default (everything on) format for monitors */
	public static final ProgressFormat DEFAULT = new ProgressFormat(true, true, true, true, true);
	
	private final boolean showTask;
	private final boolean showWork;
	private final boolean showPercent;
	private final boolean showStatus;
	private final boolean showParents;
	
	public ProgressFormat(boolean showParents, boolean showTask, boolean showWork,
			boolean showPercent, boolean showStatus) {
		super();
		this.showParents = showParents;
		this.showTask = showTask;
		this.showWork = showWork;
		this.showPercent = showPercent;
		this.showStatus = showStatus;
	}

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
			sb.append("[").append(monitor.getWorkDone()).append('/').append(monitor.getSize()).append("] ");
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
