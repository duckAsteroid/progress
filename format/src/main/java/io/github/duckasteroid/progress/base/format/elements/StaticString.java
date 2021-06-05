package io.github.duckasteroid.progress.base.format.elements;

import io.github.duckasteroid.progress.ProgressMonitor;

/**
 * Adds a fixed string to the format output. Normally used for separators/whitespace
 */
public class StaticString implements FormatElement {
  public static final StaticString CONDITIONAL_WHITESPACE = new StaticString(" ", false);
  public static final StaticString WHITESPACE = new StaticString(" ", true);
  /**
   * should this be printed even if empty.
   */
  private final transient boolean appendWhenEmpty;
  private final transient String separator;

  public StaticString(String separator) {
    this(separator, true);
  }

  public StaticString(String separator, boolean appendWhenEmpty) {
    this.separator = separator;
    this.appendWhenEmpty = appendWhenEmpty;
  }

  @Override
  public void appendTo(StringBuilder sb, ProgressMonitor monitor) {
    if (sb.length() == 0 && !appendWhenEmpty) {
      return;
    }
    sb.append(separator);
  }
}
