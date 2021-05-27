package io.github.duckasteroid.progress.base.format.elements;

import io.github.duckasteroid.progress.ProgressMonitor;

/**
 * Adds a constantly changing character to the format output. Aimed to evoke a spinner
 * NOTE: This string changes every time the format is output regardless of the monitor
 */
public class Spinner implements FormatElement {
    /** index for spinner updates */
    private int spindex = 0;
    /** a sequence of characters for the spinner - looped over */
    private final char[] spinnerChars;

    /** A char pattern that does a really boring spinner with slash chars */
    public static final char[] SPINNER_SLASHES = new char[]{ '-', '\\', '|', '/'};

    public Spinner(char[] spinnerChars) {
        if (spinnerChars == null)
            throw new IllegalArgumentException("Spinner chars cannot be null");
        if (spinnerChars.length < 2)
            throw new IllegalArgumentException("Spinner chars length must be 2 or more");

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
