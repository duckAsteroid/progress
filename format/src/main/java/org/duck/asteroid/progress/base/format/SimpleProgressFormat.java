package org.duck.asteroid.progress.base.format;

import org.duck.asteroid.progress.ProgressMonitor;
import org.duck.asteroid.progress.base.format.elements.*;

import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * A simple class that turns a {@link ProgressMonitor} into a {@link String} for typical human consumption
 */
public class SimpleProgressFormat implements ProgressFormat {
	/** A default (everything on) format for monitors */
	public static final ProgressFormat DEFAULT = createSimpleProgressFormat(true, true, true, true, true);

	/** include a <code>&gt;</code> separated list of parents */
	private final boolean showParents;

	private ProgressFormat format;
	
	private SimpleProgressFormat(boolean showParents, ProgressFormat format) {
		this.showParents = showParents;
		this.format = format;
	}

	/** A number format for percentages */
	protected static final NumberFormat PERCENT_FMT = NumberFormat.getPercentInstance();

	public static SimpleProgressFormat createSimpleProgressFormat(boolean showParents, boolean showTask, boolean showWork,
																  boolean showPercent, boolean showStatus) {
		ArrayList<FormatElement> elements = new ArrayList(8);
		if (showTask) {
			elements.add(new TaskName());
		}
		if (showWork) {
			if (!elements.isEmpty()){
				elements.add(StaticString.CONDITIONAL_WHITESPACE);
			}
			elements.add(new StringWrapper("[", new Fraction(), "]"));
		}
		if (showPercent) {
			if (!elements.isEmpty()){
				elements.add(StaticString.CONDITIONAL_WHITESPACE);
			}
			elements.add(new StringWrapper("(", new Percentage(), ")"));
		}
		if (showStatus) {
			if (!elements.isEmpty()){
				elements.add(new StaticString(" - ", false));
			}
			elements.add(new Status());
		}
		return new SimpleProgressFormat(showParents, new CompoundFormat(elements));
	}

	@Override
	public String format(ProgressMonitor monitor) {
		final StringBuilder sb = new StringBuilder();

		if (showParents && monitor.getParent() != null) {
			// reurse
			String parent = format(monitor.getParent());
			sb.append(parent).append("> ");
		}

		sb.append(format.format(monitor));

		return sb.toString();
	}
}
