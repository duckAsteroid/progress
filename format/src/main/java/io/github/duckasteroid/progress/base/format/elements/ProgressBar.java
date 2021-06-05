package io.github.duckasteroid.progress.base.format.elements;

import io.github.duckasteroid.progress.ProgressMonitor;

import java.util.Arrays;

/**
 * Adds a textual progress bar to the format output
 */
public class ProgressBar implements FormatElement {
    /** A bar pattern like this: <code>====&gt;   </code> */
    public static final char[] BAR_EQUALS = new char[] {'=', '>', ' '};
    private static final int MIN_WIDTH = 1;
    private static final int MIN_BAR_CHARS = 3;

    /** show a "fake" character progress bar using the first 3 chars from this array */
    private transient final char[] barChars;
    private transient final String fullBar;
    private transient final String emptyBar;

    /** how wide (in characters) to make the formatted message */
    private transient final int width;

    public ProgressBar(int width, char[] barChars ) {
        if (width <= MIN_WIDTH)
            throw new IllegalArgumentException("Width must be > 1");
        this.width = width;
        this.barChars = barChars;
        if (barChars != null) {
            if (barChars.length >= MIN_BAR_CHARS) {
                // create full length strings for the bar to avoid doing on every progress update
                char[] tmp = new char[width];
                Arrays.fill(tmp, barChars[0]);
                fullBar = new String(tmp);
                Arrays.fill(tmp, barChars[2]);
                emptyBar = new String(tmp);
            }
            else {
                throw new IllegalArgumentException("Bar chars should contain 4 chars");
            }
        }
        else {
            throw new IllegalArgumentException("Bar chars should not be null");
        }
    }

    @Override
    public void appendTo(StringBuilder sb, ProgressMonitor monitor) {
        int percent = Math.max(0, (int) (monitor.getWorkDone() * width / monitor.getSize()));
        sb.append(fullBar.substring(width - percent))
            .append(barChars[1])
            .append(emptyBar.substring(percent));
    }
}
