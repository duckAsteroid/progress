package io.github.duckasteroid.progress.base.format.elements;

import io.github.duckasteroid.progress.ProgressMonitor;

/**
 * Wraps another element in before/after strings.
 */
public class StringWrapper implements FormatElement {
  private final transient String before;
  private final transient String after;
  private final transient FormatElement wrapped;

  /**
   * Create string wrapper segment.
   * @param before the string before
   * @param wrapped the wrapped element
   * @param after the string after
   */
  public StringWrapper(String before, FormatElement wrapped, String after) {
    this.before = before;
    this.wrapped = wrapped;
    this.after = after;
  }

  public static StringWrapper prefix(String prefix, FormatElement wrapped) {
    return new StringWrapper(prefix, wrapped, "");
  }

  public static FormatElement wrap(String before, FormatElement wrap, String after) {
    return new StringWrapper(before, wrap, after);
  }

  @Override
  public void appendTo(StringBuilder sb, ProgressMonitor monitor) {
    if (wrapped.hasContent(monitor)) {
      sb.append(before);
      wrapped.appendTo(sb, monitor);
      sb.append(after);
    }
  }

  @Override
  public boolean hasContent(ProgressMonitor monitor) {
    return wrapped.hasContent(monitor);
  }
}
