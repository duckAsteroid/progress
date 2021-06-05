package io.github.duckasteroid.progress.base.format;

import io.github.duckasteroid.progress.ProgressMonitor;
import io.github.duckasteroid.progress.base.format.elements.FormatElement;
import io.github.duckasteroid.progress.base.format.elements.Fraction;
import io.github.duckasteroid.progress.base.format.elements.Percentage;
import io.github.duckasteroid.progress.base.format.elements.StaticString;
import io.github.duckasteroid.progress.base.format.elements.Status;
import io.github.duckasteroid.progress.base.format.elements.StringWrapper;
import io.github.duckasteroid.progress.base.format.elements.TaskName;
import io.github.duckasteroid.progress.base.format.elements.Unit;
import java.util.ArrayList;

/**
 * A simple class that turns a {@link ProgressMonitor} into a {@link String} for typical human
 * consumption.
 */
public class SimpleProgressFormat implements ProgressFormat {
  /**
   * A default (everything on) format for monitors.
   */
  public static final ProgressFormat DEFAULT =
      createSimpleProgressFormat(true, true, true, true, true, true);

  /**
   * include a <code>&gt;</code> separated list of parents.
   */
  private final transient boolean showParents;

  private final transient ProgressFormat format; //NOPMD

  private SimpleProgressFormat(boolean showParents, ProgressFormat format) {
    this.showParents = showParents;
    this.format = format;
  }

  /**
   * Create a simple progress format.
   * @param showParents show parent monitors
   * @param showTask show task name
   * @param showWork show work done absolute
   * @param showUnit show work unit
   * @param showPercent show percent work done
   * @param showStatus show status message
   * @return a new {@link SimpleProgressFormat}
   */
  public static SimpleProgressFormat createSimpleProgressFormat(boolean showParents,
                                                                boolean showTask, boolean showWork,
                                                                boolean showUnit,
                                                                boolean showPercent,
                                                                boolean showStatus) {
    ArrayList<FormatElement> elements = new ArrayList<>(8);
    if (showTask) {
      elements.add(new TaskName());
    }
    if (showWork) {
      if (!elements.isEmpty()) {
        elements.add(StaticString.CONDITIONAL_WHITESPACE);
      }
      elements.add(new StringWrapper("[", new Fraction(), "]"));
    }
    if (showUnit) {
      if (!elements.isEmpty()) {
        elements.add(StaticString.CONDITIONAL_WHITESPACE);
      }
      elements.add(new Unit());
    }
    if (showPercent) {
      if (!elements.isEmpty()) {
        elements.add(StaticString.CONDITIONAL_WHITESPACE);
      }
      elements.add(new StringWrapper("(", new Percentage(), ")"));
    }
    if (showStatus) {
      if (!elements.isEmpty()) {
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
