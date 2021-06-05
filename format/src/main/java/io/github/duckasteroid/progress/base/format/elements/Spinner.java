package io.github.duckasteroid.progress.base.format.elements;

import io.github.duckasteroid.progress.ProgressMonitor;

/**
 * Adds a constantly changing character to the format output. Aimed to evoke a spinner
 * NOTE: This string changes every time the format is output regardless of the monitor
 */
public class Spinner implements FormatElement {
  /**
   * A char pattern that does a really boring spinner with slash chars.
   */
  public static final char[] SPINNER_SLASHES = new char[] {'-', '\\', '|', '/'};
  private static final int MIN_SPIN_CHARS = 2;
  /**
   * a sequence of characters for the spinner - looped over.
   */
  private final transient char[] spinnerChars;
  /**
   * index for spinner updates.
   */
  private transient int spindex = 0;

  /**
   * Create a spinner.
   * @param spinnerChars the characters to use for the spinner
   */
  public Spinner(char[] spinnerChars) {
    if (spinnerChars == null) {
      throw new IllegalArgumentException("Spinner chars cannot be null");
    }
    if (spinnerChars.length < MIN_SPIN_CHARS) {
      throw new IllegalArgumentException("Spinner chars length must be 2 or more");
    }

    this.spinnerChars = spinnerChars;
  }

  @Override
  public void appendTo(StringBuilder sb, ProgressMonitor monitor) {
    sb.append(spinnerChars[spindex++]);
    if (spindex >= spinnerChars.length) {
      spindex = 0;
    }
  }
}
